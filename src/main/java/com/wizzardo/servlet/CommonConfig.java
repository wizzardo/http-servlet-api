package com.wizzardo.servlet;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by wizzardo on 18.01.15.
 */
public class CommonConfig extends HashMap<String, String> {
    protected Context context;
    protected String name;

    public CommonConfig(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    public ServletContext getServletContext() {
        return context;
    }

    public String getInitParameter(String s) {
        return get(s);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(keySet());
    }
}
