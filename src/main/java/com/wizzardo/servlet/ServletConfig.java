package com.wizzardo.servlet;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * @author: wizzardo
 * Date: 12/24/12
 */
public class ServletConfig extends HashMap<String, String> implements javax.servlet.ServletConfig {
    private String servletName;

    public ServletConfig(String servletName) {
        this.servletName = servletName;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getInitParameter(String s) {
        return get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(keySet());
    }
}
