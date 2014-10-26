package com.wizzardo.servlet;

import com.wizzardo.http.HttpServer;
import com.wizzardo.tools.http.HttpClient;
import com.wizzardo.tools.http.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.StdErrLog;
import org.junit.After;
import org.junit.Before;

import java.util.Properties;

/**
 * @author: wizzardo
 * Date: 23.10.14
 */
public class ServerTest {
    protected CustomServlet servlet;
    private Server jetty;
    private HttpServer myServer;
    private int jettyPort = 8080;
    private int myPort = 8081;

    @Before
    public void setUp() throws Exception {
        Properties loggingProperties = new Properties();
        loggingProperties.setProperty("org.eclipse.jetty.LEVEL", "WARN");
        StdErrLog.setProperties(loggingProperties);

        jetty = new Server(jettyPort);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/servlet");
        jetty.setHandler(context);

        servlet = new CustomServlet();
        context.addServlet(new ServletHolder(servlet), "/*");
        jetty.start();


        myServer = new HttpServer(myPort);
        myServer.setIoThreadsCount(1);
        myServer.setHandler(new ServletHandler(servlet));

        myServer.start();
    }

    @After
    public void tearDown() throws Exception {
        jetty.stop();
        servlet.clean();
    }

    protected com.wizzardo.tools.http.Request jettyRequest(String path) {
        return HttpClient.createRequest("http://localhost:" + jettyPort + path)
                .header("Connection", "Close");
    }

    protected com.wizzardo.tools.http.Request myRequest(String path) {
        return HttpClient.createRequest("http://localhost:" + myPort + path)
                .header("Connection", "Close");
    }

    public void printHeaders(Response response) {
        response.headers().forEach((s, strings) -> {
            System.out.println(s + ": " + strings);
        });
    }
}
