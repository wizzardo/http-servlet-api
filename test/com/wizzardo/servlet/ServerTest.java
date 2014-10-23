package com.wizzardo.servlet;

import com.wizzardo.tools.http.HttpClient;
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
    private int jettyPort = 8080;

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

    public int jettyPort() {
        return jettyPort;
    }
}
