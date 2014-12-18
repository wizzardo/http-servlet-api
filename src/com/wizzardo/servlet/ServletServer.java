package com.wizzardo.servlet;

import com.wizzardo.http.HttpServer;
import com.wizzardo.http.response.Status;
import com.wizzardo.tools.io.ZipTools;
import com.wizzardo.tools.misc.UncheckedThrow;
import com.wizzardo.tools.xml.Node;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wizzardo
 * Date: 03.11.14
 */
public class ServletServer<T extends ServletHttpConnection> extends HttpServer<T> {
    protected Map<String, Context> contexts = new ConcurrentHashMap<>();

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

    protected void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, ServletException {
        String path = httpRequest.path();
        Context context = getContext(path);

        if (!context.getContextPath().equals("/"))
            path = path.substring(context.getContextPath().length());

        Servlet servlet = context.getServletsMapping().get(path);
        httpRequest.setContext(context);
        httpResponse.setContext(context);
        if (servlet == null) {
            httpResponse.setStatus(Status._404);
        } else {
            servlet.service(httpRequest, httpResponse);
            if (httpResponse.hasWriter())
                httpResponse.setBody(httpResponse.getData());
        }
    }

    protected Context getContext(String path) {
        int i = path.indexOf("/", 1);
        if (i == -1)
            return contexts.get("/");
        else
            return contexts.get(path.substring(0, i));
    }


    public synchronized ServletServer append(String contextPath, String path, Servlet servlet) {
        Context context = contexts.get(contextPath);
        if (context == null) {
            context = new Context(getHost(), getPort(), contextPath);
            contexts.put(contextPath, context);
        }
        context.getServletsMapping().append(path, servlet);
        return this;
    }

    public void registerWar(String path) {
        try {
            File war = new File(path);
            File unpacked = new File("/tmp/" + war.getName());
            ZipTools.unzip(war, unpacked);

            File webXML = new File(unpacked, "WEB-INF/web.xml");
            Node webXmlNode = Node.parse(webXML);
//        System.out.println(webXmlNode);

            String appBase = war.getName().equalsIgnoreCase("root.war") ? "/" : "/" + war.getName().substring(0, war.getName().length() - 4);

            ClassLoader cl = URLClassLoader.newInstance(new URL[]{new File(unpacked, "WEB-INF/classes").toURI().toURL()});
//        cl.loadClass(webXmlNode.findChildWithNameEquals("servlet",true).findChildsWithNameEquals("servlet-class").toString())
//        System.out.println(webXmlNode.findChildWithNameEquals("servlet",true).findChildWithNameEquals("servlet-class",true).getText());
//        System.out.println("try to get servlets");
//        System.out.println(webXmlNode);
            for (Node servletNode : webXmlNode.findAll("servlet")) {
//        for(Node params:servletNode.getInnerNodes()){
//            System.out.println(params.getName());
//        }

                Class clazz = cl.loadClass(servletNode.get("servlet-class").text());
                Servlet servlet = (Servlet) clazz.newInstance();
                String servletName = servletNode.get("servlet-name").text();

                ServletConfig servletConfig = new ServletConfig(servletName);
                for (Node param : servletNode.getAll("init-param")) {
                    servletConfig.put(param.get("param-name").text(), param.get("param-value").text());
                }
                servlet.init(servletConfig);
//            System.out.println("servletName: "+servletName);
                Node m = webXmlNode.get("servlet-mapping/servlet-name[text()=" + servletName + "]").parent();
                for (Node urlPattern : m.getAll("url-pattern")) {
//                System.out.println("map to "+appBase + urlPattern.text());
                    append(appBase, urlPattern.text(), servlet);
                }
            }
        } catch (Exception e) {
            throw UncheckedThrow.rethrow(e);
        }
    }
}
