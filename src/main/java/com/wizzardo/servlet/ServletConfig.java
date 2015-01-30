package com.wizzardo.servlet;

/**
 * @author: wizzardo
 * Date: 12/24/12
 */
public class ServletConfig extends CommonConfig implements javax.servlet.ServletConfig {

    public ServletConfig(Context context, String name) {
        super(context, name);
    }

    @Override
    public String getServletName() {
        return name;
    }
}
