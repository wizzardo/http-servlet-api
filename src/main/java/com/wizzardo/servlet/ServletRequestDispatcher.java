package com.wizzardo.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by wizzardo on 08.02.15.
 */
public class ServletRequestDispatcher implements RequestDispatcher {

    private Servlet servlet;

    public ServletRequestDispatcher(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (response.isCommitted())
            throw new IllegalStateException("the response has already been committed");

        servlet.service(request, response);
        response.flushBuffer();
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
