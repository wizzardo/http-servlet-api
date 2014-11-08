package com.wizzardo.servlet;

/**
 * @author: wizzardo
 * Date: 31.10.14
 */
public class Context {
    protected String host;
    protected int port;
    protected String contextPath;

    public Context(String host, int port, String contextPath) {
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
    }

    public ServletServer createServer() {
        return new ServletServer(this);
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
