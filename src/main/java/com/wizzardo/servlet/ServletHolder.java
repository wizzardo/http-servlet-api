package com.wizzardo.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * Created by wizzardo on 13.02.15.
 */
public class ServletHolder {
    private volatile boolean initialized = false;

    private Servlet servlet;
    private ServletConfig servletConfig;
    private Context context;

    final int loadOnStartup;
    final int order;

    public ServletHolder(Servlet servlet, ServletConfig servletConfig, Context context, int loadOnStartup, int order) {
        this.servlet = servlet;
        this.servletConfig = servletConfig;
        this.context = context;
        this.loadOnStartup = loadOnStartup;
        this.order = order;
    }

    public ServletHolder(Servlet servlet, ServletConfig servletConfig, Context context) {
        this(servlet, servletConfig, context, -1, -1);
    }

    public Servlet get() throws ServletException {
        init();
        return servlet;
    }

    public void init() throws ServletException {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    servlet.init(servletConfig);
                    context.addServletToDestroy(servlet);
                    initialized = true;
                }
            }
        }
    }
}
