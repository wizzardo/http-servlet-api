package com.wizzardo.servlet;

import javax.servlet.*;

/**
 * @author: wizzardo
 * Date: 22.12.14
 */
public class AsyncContext implements javax.servlet.AsyncContext {

    private final ServletRequest request;
    private final ServletResponse response;
    private final boolean original;

    AsyncContext(ServletRequest request, ServletResponse response, boolean original) {
        this.request = request;
        this.response = response;
        this.original = original;
    }

    @Override
    public ServletRequest getRequest() {
        return request;
    }

    @Override
    public ServletResponse getResponse() {
        return response;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return original;
    }

    @Override
    public void dispatch() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void dispatch(String path) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void complete() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void start(Runnable run) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void addListener(AsyncListener listener) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setTimeout(long timeout) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public long getTimeout() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
