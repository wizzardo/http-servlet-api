package com.wizzardo.servlet;

/**
 * @author: wizzardo
 * Date: 12/24/12
 */
public class ServletConfig extends CommonConfig implements javax.servlet.ServletConfig {

    public ServletConfig(String servletName) {
        super(servletName);
    }

    @Override
    public String getServletName() {
        return name;
    }
}
