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
    AtomicInteger inits = new AtomicInteger();
    AtomicInteger destoryes = new AtomicInteger();

    @Override
    protected void init() throws IOException {
        super.init();

        filter = new CustomFilter();
        filter.onInit = it -> {
            inits.incrementAndGet();
            return null;
        };
        filter.onDestroy = () -> {
            destoryes.incrementAndGet();
        };
        ((ServletContextHandler) jetty.getHandler()).addFilter(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Assert.assertEquals(1, inits.get());
        Assert.assertEquals(0, destoryes.get());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        Assert.assertEquals(1, inits.get());
        Assert.assertEquals(1, destoryes.get());
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
