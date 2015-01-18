package com.wizzardo.servlet;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by wizzardo on 18.01.15.
 */
public class CommonConfig extends HashMap<String, String> {
    protected String name;

    public CommonConfig(String name) {
        this.name = name;
    }

    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public String getInitParameter(String s) {
        return get(s);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(keySet());
    }
}
