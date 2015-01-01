package com.wizzardo.servlet;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wizzardo on 01.01.15.
 */
public class FilterTest extends ServerTest {

    CustomFilter filter;

    @Override
    protected void init() {
        super.init();

        filter = new CustomFilter();
        filter.onInit = it -> {
            System.out.println("onInit");
            return null;
        };
        filter.onDestroy = () -> {
            System.out.println("onDestroy");
        };
        ((ServletContextHandler) jetty.getHandler()).addFilter(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
    }

    @Test
    public void filterTest() throws IOException {
        AtomicInteger counter = new AtomicInteger();
        filter.handler = (req, resp, chain) -> {
            Assert.assertEquals("/foo/bar", ((HttpServletRequest) req).getServletPath());
            counter.incrementAndGet();
        };
        jettyRequest(CONTEXT_PATH + "/foo/bar").get().asString();
        Assert.assertEquals(1, counter.get());
    }
}
