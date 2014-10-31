package com.wizzardo.servlet;

import com.wizzardo.http.HttpServer;

/**
 * @author: wizzardo
 * Date: 31.10.14
 */
public class Context {
    private HttpServer server;
    private String contextPath;

    public Context(HttpServer server, String contextPath) {
        this.server = server;
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getHost() {
        return server.getHost();
    }

    public int getPort() {
        return server.getPort();
    }
}
