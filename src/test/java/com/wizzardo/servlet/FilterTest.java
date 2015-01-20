package com.wizzardo.servlet;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
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
    protected void init() throws IOException, ServletException {
        super.init();

        filter = new CustomFilter();
        filter.onInit = it -> {
            inits.incrementAndGet();
            return null;
        };
        filter.onDestroy = () -> {
            destoryes.incrementAndGet();
        };
        ((ServletContextHandler) jetty.getHandler()).addFilter(new FilterHolder(filter), servletPath + "*", EnumSet.of(DispatcherType.REQUEST));

        myServer.append(contextPath, servletPath + "*", filter);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Assert.assertEquals(2, inits.get());
        Assert.assertEquals(0, destoryes.get());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        Assert.assertEquals(2, inits.get());
        Assert.assertEquals(2, destoryes.get());
    }

    @Test
    public void filterTest() throws IOException {
        AtomicInteger counter = new AtomicInteger();
        filter.handler = (req, resp, chain) -> {
            counter.incrementAndGet();
            System.out.println("before chain");
            if (counter.get() > 2)
                chain.doFilter(req, resp);
            else
                resp.getWriter().write("filtered");
            System.out.println("after chain");
        };
        servlet.get = (req, resp) -> {
            System.out.println("servlet");
            resp.getWriter().write("ok");
        };
        Assert.assertEquals("filtered", jettyRequest().get().asString());
        Assert.assertEquals(1, counter.get());
        Assert.assertEquals("filtered", myRequest().get().asString());
        Assert.assertEquals(2, counter.get());

        Assert.assertEquals("ok", jettyRequest().get().asString());
        Assert.assertEquals(3, counter.get());
        Assert.assertEquals("ok", myRequest().get().asString());
        Assert.assertEquals(4, counter.get());
        ;
    }
}
