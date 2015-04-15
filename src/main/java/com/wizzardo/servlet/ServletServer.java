package com.wizzardo.servlet;

import com.wizzardo.http.HttpServer;
import com.wizzardo.http.mapping.ChainUrlMapping;
import com.wizzardo.http.mapping.Path;
import com.wizzardo.http.response.Status;
import com.wizzardo.tools.io.ZipTools;
import com.wizzardo.tools.misc.Unchecked;
import com.wizzardo.tools.xml.Node;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wizzardo
 * Date: 03.11.14
 */
public class ServletServer<T extends ServletHttpConnection> extends HttpServer<T> {
    protected Map<String, Context> contexts = new ConcurrentHashMap<>();
    protected Context rootContext;

    public ServletServer(String ip, int port) {
        super(ip, port);
    }

    public ServletServer(String ip, int port, int workers) {
        super(ip, port, workers);
    }

    @Override
    protected T createConnection(int fd, int ip, int port) {
        return (T) new ServletHttpConnection(fd, ip, port, this);
    }

    protected void handle(T connection) {
        try {
            HttpRequest httpRequest = connection.getRequest();
            HttpResponse httpResponse = connection.getResponse();
            httpResponse.setRequest(httpRequest);

            processRequest(httpRequest, httpResponse);
        } catch (Exception t) {
            t.printStackTrace();
            //TODO render error page
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finishHandling(T connection) throws IOException {
        if (connection.getRequest().isAsyncStarted())
            return;

        super.finishHandling(connection);
    }

    @Override
    public void stopEpoll() {
        super.stopEpoll();
        for (Context context : contexts.values())
            context.onDestroy();
    }

    protected void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, ServletException {
        Path path = httpRequest.path();
        Context context = getContext(path);
        if (context == null) {
            httpResponse.setStatus(Status._404);
            return;
        }

        if (!context.getContextPath().equals("/"))
            path = path.subPath(1);

        httpRequest.setServletPath(path);
        httpRequest.setContext(context);
        httpResponse.setContext(context);

        ServletHolder servletHolder = context.getServletHolder(httpRequest, path);
        httpRequest.setCurrentServlet(servletHolder);

        ChainUrlMapping.Chain<Filter> filters = context.getFiltersMapping().get(httpRequest, path);
        if (filters != null) {
            Iterator<Filter> filterIterator = filters.iterator();
            if (filterIterator.hasNext()) {
                filterIterator.next().doFilter(httpRequest, httpResponse, new FilterChain() {
                    @Override
                    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                        if (filterIterator.hasNext())
                            filterIterator.next().doFilter(request, response, this);
                        else
                            processServlet(servletHolder.get(), (HttpServletRequest) request, (HttpServletResponse) response);
                    }
                });
            }
        } else
            processServlet(servletHolder.get(), httpRequest, httpResponse);

        if (!httpRequest.isAsyncStarted() && httpResponse.hasWriter())
            httpResponse.setBody(httpResponse.getData());
    }


    protected void processServlet(Servlet servlet, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        if (servlet == null)
            httpResponse.setStatus(Status._404.code);
        else
            servlet.service(httpRequest, httpResponse);
    }

    protected Context getContext(Path path) {
        if (path.length() == 0)
            return rootContext;
        return findContext(path.getPart(0));
    }

    protected Context getContext(String path) {
        if (path.length() == 0 || path.equals("/"))
            return rootContext;

        return findContext(path.split("/", 3)[1]);
    }

    protected Context findContext(String context) {
        Context c = contexts.get(context);
        return c == null ? rootContext : c;
    }

    protected Context getOrCreateContext(String contextPath) {
        if (contextPath.startsWith("/"))
            contextPath = contextPath.substring(1);

        Context context = contexts.get(contextPath);
        if (context == null)
            context = createContext(contextPath);

        return context;
    }

    public synchronized ServletServer append(String contextPath, String path, Servlet servlet) throws ServletException {
        Context context = getOrCreateContext(contextPath);
        context.addServletHolder(path, new ServletHolder(servlet, new ServletConfig(context, servlet.getClass().getCanonicalName() + "-" + servlet.hashCode()), context));
        return this;
    }

    public synchronized ServletServer append(String contextPath, String path, Filter filter) throws ServletException {
        Context context = getOrCreateContext(contextPath);
        context.getFiltersMapping().add(path, filter);
        filter.init(new FilterConfig(context, filter.getClass().getCanonicalName() + "-" + filter.hashCode()));
        context.addFilterToDestroy(filter);
        return this;
    }

    public synchronized Context createContext(String contextPath) {
        if (contextPath.equals("/"))
            contextPath = "";

        Context context = new Context(this, "/" + contextPath);
        if (contextPath.isEmpty())
            rootContext = context;
        else if (contexts.putIfAbsent(contextPath, context) != null)
            throw new IllegalStateException("Context with name '" + contextPath + "' was already initialized");
        return context;
    }

    public void registerWar(String path) {
        File war = new File(path);
        String appBase = war.getName().equalsIgnoreCase("root.war") ? "" : war.getName().substring(0, war.getName().length() - 4);
        registerWar(war, appBase);
    }

    public void registerWar(File war, String appBase) {
        try {
            File unpacked = new File("/tmp/" + war.getName() + "_");
            ZipTools.unzip(war, unpacked);

            File webXML = new File(unpacked, "WEB-INF/web.xml");
            Node webXmlNode = Node.parse(webXML);
//        System.out.println(webXmlNode);


            File libsDir = new File(unpacked, "WEB-INF/lib");
            File[] libs = libsDir.isDirectory() ? libsDir.listFiles() : null;
            URL[] resources = new URL[1 + (libs != null ? libs.length : 0)];
            if (libsDir.exists()) {
                for (int i = 0; i < libs.length; i++) {
                    resources[i] = libs[i].toURI().toURL();
                }
            }
            resources[resources.length - 1] = new File(unpacked, "WEB-INF/classes").toURI().toURL();


            ClassLoader cl = URLClassLoader.newInstance(resources);
//        cl.loadClass(webXmlNode.findChildWithNameEquals("servlet",true).findChildsWithNameEquals("servlet-class").toString())
//        System.out.println(webXmlNode.findChildWithNameEquals("servlet",true).findChildWithNameEquals("servlet-class",true).getText());
//        System.out.println("try to get servlets");
//        System.out.println(webXmlNode);

            Thread.currentThread().setContextClassLoader(cl);

            Context context = createContext(appBase);
            context.setContextDir(unpacked);
            for (Node listenerNode : webXmlNode.findAll("listener")) {
                Class clazz = cl.loadClass(listenerNode.get("listener-class").text());
                context.addContextListener((ServletContextListener) clazz.newInstance());
            }

            for (Node contextParam : webXmlNode.findAll("context-param")) {
                context.setInitParameter(contextParam.get("param-name").text(), contextParam.get("param-value").text());
            }
            context.onInit();

            int servletCounter = 0;
            List<ServletHolder> servletsToInit = new ArrayList<>();

            for (Node servletNode : webXmlNode.findAll("servlet")) {
                Class clazz = cl.loadClass(servletNode.get("servlet-class").text());
                Servlet servlet = (Servlet) clazz.newInstance();
                String servletName = servletNode.get("servlet-name").text();

                ServletConfig servletConfig = new ServletConfig(context, servletName);
                for (Node param : servletNode.getAll("init-param")) {
                    servletConfig.put(param.get("param-name").text(), param.get("param-value").text());
                }
                Node loadOnStartup = servletNode.get("load-on-startup");
                int los = -1;
                if (loadOnStartup != null)
                    los = Integer.parseInt(loadOnStartup.text());

                boolean isAsync = false;
                Node asyncSupported = servletNode.get("async-supported");
                if (asyncSupported != null)
                    isAsync = Boolean.parseBoolean(asyncSupported.text());

                ServletHolder servletHolder = new ServletHolder(servlet, servletConfig, context, los, servletCounter++, isAsync);
                if (servletHolder.loadOnStartup > 0)
                    servletsToInit.add(servletHolder);

                Node m = webXmlNode.get("servlet-mapping/servlet-name[text()=" + servletName + "]").parent();
                for (Node urlPattern : m.getAll("url-pattern")) {
                    context.addServletHolder(urlPattern.text(), servletHolder);
                }
            }

            for (Node filterNode : webXmlNode.findAll("filter")) {
                Class clazz = cl.loadClass(filterNode.get("filter-class").text());
                Filter filter = (Filter) clazz.newInstance();
                String filterName = filterNode.get("filter-name").text();

                FilterConfig filterConfig = new FilterConfig(context, filterName);
                for (Node param : filterNode.getAll("init-param")) {
                    filterConfig.put(param.get("param-name").text(), param.get("param-value").text());
                }
                filter.init(filterConfig);
                context.addFilterToDestroy(filter);
                Node m = webXmlNode.get("filter-mapping/filter-name[text()=" + filterName + "]").parent();
                for (Node urlPattern : m.getAll("url-pattern")) {
                    context.getFiltersMapping().add(urlPattern.text(), filter);
                }
            }

            servletsToInit.sort(new Comparator<ServletHolder>() {
                @Override
                public int compare(ServletHolder o1, ServletHolder o2) {
                    int r = Integer.compare(o1.loadOnStartup, o2.loadOnStartup);
                    if (r == 0)
                        r = Integer.compare(o1.order, o2.order);
                    return r;
                }
            });

            for (ServletHolder holder : servletsToInit)
                holder.init();

        } catch (Exception e) {
            throw Unchecked.rethrow(e);
        }
    }
}
