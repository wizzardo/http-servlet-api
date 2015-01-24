package com.wizzardo.servlet;

import com.wizzardo.http.ChainUrlMapping;
import com.wizzardo.http.HttpServer;
import com.wizzardo.http.Path;
import com.wizzardo.http.response.Status;
import com.wizzardo.tools.io.ZipTools;
import com.wizzardo.tools.misc.UncheckedThrow;
import com.wizzardo.tools.xml.Node;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
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
        return (T) new ServletHttpConnection(fd, ip, port);
    }

    protected void handle(T connection) {
        try {
            HttpRequest httpRequest = connection.getRequest();
            HttpResponse httpResponse = connection.getResponse();
            httpResponse.setRequest(httpRequest);

            processRequest(httpRequest, httpResponse);

            finishHandling(connection);
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

        if (!context.getContextPath().isEmpty())
            path = path.subPath(1);

        httpRequest.setContext(context);
        httpResponse.setContext(context);

        Servlet servlet = context.getServletsMapping().get(httpRequest, path);
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
                            processServlet(servlet, (HttpServletRequest) request, (HttpServletResponse) response);
                    }
                });
            }
        } else
            processServlet(servlet, httpRequest, httpResponse);

        if (httpResponse.hasWriter())
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
        else {
            Context context = contexts.get(path.getPart(0));
            if (context == null)
                return rootContext;
            else
                return context;
        }
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
        context.getServletsMapping().append(path, servlet);
        servlet.init(new ServletConfig(servlet.getClass().getCanonicalName() + "-" + servlet.hashCode()));
        context.addServletToDestroy(servlet);
        return this;
    }

    public synchronized ServletServer append(String contextPath, String path, Filter filter) throws ServletException {
        Context context = getOrCreateContext(contextPath);
        context.getFiltersMapping().add(path, filter);
        filter.init(new FilterConfig(filter.getClass().getCanonicalName() + "-" + filter.hashCode()));
        context.addFilterToDestroy(filter);
        return this;
    }

    public synchronized Context createContext(String contextPath) {
        Context context = new Context(getHost(), getPort(), "/" + contextPath);
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

            for (Node servletNode : webXmlNode.findAll("servlet")) {
                Class clazz = cl.loadClass(servletNode.get("servlet-class").text());
                Servlet servlet = (Servlet) clazz.newInstance();
                String servletName = servletNode.get("servlet-name").text();

                ServletConfig servletConfig = new ServletConfig(servletName);
                for (Node param : servletNode.getAll("init-param")) {
                    servletConfig.put(param.get("param-name").text(), param.get("param-value").text());
                }
                servlet.init(servletConfig);
                context.addServletToDestroy(servlet);
                Node m = webXmlNode.get("servlet-mapping/servlet-name[text()=" + servletName + "]").parent();
                for (Node urlPattern : m.getAll("url-pattern")) {
                    context.getServletsMapping().append(urlPattern.text(), servlet);
                }
            }

            for (Node filterNode : webXmlNode.findAll("filter")) {
                Class clazz = cl.loadClass(filterNode.get("filter-class").text());
                Filter filter = (Filter) clazz.newInstance();
                String filterName = filterNode.get("filter-name").text();

                FilterConfig filterConfig = new FilterConfig(filterName);
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
        } catch (Exception e) {
            throw UncheckedThrow.rethrow(e);
        }
    }
}
