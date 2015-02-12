package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wizzardo on 12.02.15.
 */
public class TestServletInitOrder extends WarTest {

    static AtomicInteger counter = new AtomicInteger();

    @Override
    protected void customizeWar(WarBuilder builder) {
        servletPath = "/ok";
        builder.addClass(FooServlet.class);
        builder.addClass(BarServlet.class);
        builder.addClass(FooBarServlet.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(FooServlet.class).url("/foo").loadOnStartup(2))
                .append(new WarBuilder.ServletMapping(BarServlet.class).url("/bar").loadOnStartup(1))
                .append(new WarBuilder.ServletMapping(FooBarServlet.class).url("/foobar").loadOnStartup(0))
        ;
    }

    @Test
    public void testOk() throws IOException {
        Assert.assertEquals("foo_3", jettyRequest(contextPath + "/foo").get().asString());
        Assert.assertEquals("bar_2", jettyRequest(contextPath + "/bar").get().asString());
        Assert.assertEquals("foobar_1", jettyRequest(contextPath + "/foobar").get().asString());
//        Assert.assertEquals("ok", myRequest().get().asString());
    }

    public static class FooServlet extends HttpServlet {
        protected String value;

        @Override
        public void init() throws ServletException {
            value = "foo_" + counter.incrementAndGet();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write(value);
        }
    }

    public static class BarServlet extends FooServlet {
        @Override
        public void init() throws ServletException {
            value = "bar_" + counter.incrementAndGet();
        }
    }

    public static class FooBarServlet extends FooServlet {
        @Override
        public void init() throws ServletException {
            value = "foobar_" + counter.incrementAndGet();
        }
    }
}
