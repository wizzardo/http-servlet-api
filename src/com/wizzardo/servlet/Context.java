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

    public boolean isSecure() {
        return false;
    }

    public String createAbsoluteUrl(String path) {
        StringBuilder sb = new StringBuilder();
        if (isSecure())
            sb.append("https://");
        else
            sb.append("http://");

        sb.append(host);
        if ((!isSecure() && port != 80) || (isSecure() && port != 443))
            sb.append(":").append(port);

        sb.append(path);
        return sb.toString();
    }
}
