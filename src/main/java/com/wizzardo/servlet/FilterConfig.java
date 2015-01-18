package com.wizzardo.servlet;

/**
 * @author: wizzardo
 * Date: 12/24/12
 */
public class FilterConfig extends CommonConfig implements javax.servlet.FilterConfig {

    public FilterConfig(String filterName) {
        super(filterName);
    }

    @Override
    public String getFilterName() {
        return name;
    }
}
