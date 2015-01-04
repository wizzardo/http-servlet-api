package com.wizzardo.servlet;

import com.wizzardo.tools.http.HttpSession;
import com.wizzardo.tools.http.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.StdErrLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author: wizzardo
 * Date: 23.10.14
 */
public class ServerTest {
    protected final String CONTEXT_PATH = "/context";
    protected final String SERVLET_PATH = "/servlet/";
    protected CustomServlet servlet;
    protected Server jetty;
    protected ServletServer myServer;
    protected int jettyPort = 9080;
    protected int myPort = 9081;
    protected HttpSession jettySession = new HttpSession();
    protected HttpSession mySession = new HttpSession();

    @Before
    public void setUp() throws Exception {
        Properties loggingProperties = new Properties();
        loggingProperties.setProperty("org.eclipse.jetty.LEVEL", "WARN");
        StdErrLog.setProperties(loggingProperties);

        createServers();
        init();
        startServers();
    }

    protected void init() throws IOException {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(CONTEXT_PATH);
        jetty.setHandler(context);

        servlet = new CustomServlet();
        context.addServlet(new ServletHolder(servlet), SERVLET_PATH + "*");


        myServer.append(CONTEXT_PATH, SERVLET_PATH + "*", servlet);
    }

    protected void startServers() throws Exception {
        jetty.start();
        myServer.start();
    }

    protected void createServers() throws Exception {
        jetty = new Server(jettyPort);

        myServer = new ServletServer("localhost", myPort, 1);
        myServer.setIoThreadsCount(1);
    }

    @After
    public void tearDown() throws Exception {
        jetty.stop();

        myServer.stopEpoll();
    }

    protected com.wizzardo.tools.http.Request jettyRequest() {
        return jettyRequest(CONTEXT_PATH + SERVLET_PATH);
    }

    protected com.wizzardo.tools.http.Request myRequest() {
        return myRequest(CONTEXT_PATH + SERVLET_PATH);
    }

    protected com.wizzardo.tools.http.Request jettyRequest(String path) {
        return createRequest(jettySession, "http://localhost:" + jettyPort + path);
    }

    protected com.wizzardo.tools.http.Request myRequest(String path) {
        return createRequest(mySession, "http://localhost:" + myPort + path);
    }

    protected com.wizzardo.tools.http.Request createRequest(HttpSession session, String url) {
        return session.createRequest(url)
                .disableRedirects()
                .header("Connection", "Close");
    }

    public void printHeaders(Response response) {
        response.headers().forEach((s, strings) -> {
            System.out.println(s + ": " + strings);
        });
    }

    protected String enumerationToString(Enumeration enumeration) {
        StringBuilder sb = new StringBuilder("[");
        while (enumeration.hasMoreElements()) {
            if (sb.length() > 1)
                sb.append(", ");
            sb.append(enumeration.nextElement());
        }
        sb.append("]");
        return sb.toString();
    }

    protected void test(TestStrategy strategy) throws IOException {
        Assert.assertEquals(strategy.exec(jettyRequest()), strategy.exec(myRequest()));
    }

    protected void testGet(CustomServlet.Handler handler) throws IOException {
        testGet(handler, 1);
    }

    protected void testGet(CustomServlet.Handler handler, int repeat) throws IOException {
        servlet.get = handler;
        for (int i = 0; i < repeat; i++) {
            test(request -> request.get().asString());
        }
    }

    protected void test(TestStrategy strategy, String path) throws IOException {
        path = CONTEXT_PATH + SERVLET_PATH + path;
        Assert.assertEquals(strategy.exec(jettyRequest(path)), strategy.exec(myRequest(path)));
    }

    protected static interface TestStrategy<T> {
        public T exec(com.wizzardo.tools.http.Request request) throws IOException;
    }
}
