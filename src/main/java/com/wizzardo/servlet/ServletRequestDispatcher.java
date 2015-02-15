package com.wizzardo.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by wizzardo on 08.02.15.
 */
public class ServletRequestDispatcher implements RequestDispatcher {

    private ServletHolder servletHolder;
    private String path;

    public ServletRequestDispatcher(ServletHolder servlet, String path) {
        this.servletHolder = servlet;
        this.path = path;
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (response.isCommitted())
            throw new IllegalStateException("the response has already been committed");

        HttpRequest req = (HttpRequest) request;
        req.setDispatcherType(DispatcherType.FORWARD);

        HttpResponse resp = (HttpResponse) response;
        if (path != null)
            servletHolder.get().service(new DispatchedRequest(req, path), response);
        else
            servletHolder.get().service(req, response);


        if (!resp.isCommitted() && resp.hasWriter())
            resp.setBody(resp.getData());

        resp.flushBuffer();
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpRequest req = (HttpRequest) request;
        req.setDispatcherType(DispatcherType.INCLUDE);

        servletHolder.get().service(request, new ReadOnlyHeadersWrapper((HttpServletResponse) response));
//        servletHolder.service(request, response);
    }

    private static class DispatchedRequest extends HttpServletRequestWrapper {
        String path;

        public DispatchedRequest(HttpServletRequest request, String path) {
            super(request);
            this.path = path;
        }

        @Override
        public String getServletPath() {
            return path;
        }
    }

    private static class ReadOnlyHeadersWrapper extends HttpServletResponseWrapper {
        public ReadOnlyHeadersWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setCharacterEncoding(String charset) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        @Override
        public void setContentType(String type) {
        }

        @Override
        public void setLocale(Locale loc) {
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void setStatus(int sc) {
        }

        @Override
        public void setStatus(int sc, String sm) {
        }

        @Override
        public void addCookie(Cookie cookie) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void addHeader(String name, String value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }
    }
}
