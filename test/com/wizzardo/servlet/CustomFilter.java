package com.wizzardo.servlet;

import com.wizzardo.tools.collections.CollectionTools;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by wizzardo on 01.01.15.
 */
public class CustomFilter implements Filter {

    volatile CollectionTools.Closure<Void, FilterConfig> onInit;
    volatile Runnable onDestroy;
    volatile Handler handler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (onInit != null)
            onInit.execute(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (handler != null) {
            handler.handle(request, response, chain);
        }
    }

    @Override
    public void destroy() {
        if (onDestroy != null)
            onDestroy.run();
    }

    public static interface Handler {
        void handle(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException;
    }
}
