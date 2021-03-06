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

/**
 * Created by wizzardo on 12.02.15.
 */
public class TestServletInitOrder extends WarTest {

    @Override
    protected void customizeWar(WarBuilder builder) {
        builder.addClass(FooServlet.class);
        builder.addClass(BarServlet.class);
        builder.addClass(BarServlet2.class);
        builder.addClass(FooBarServlet.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(FooServlet.class).url("/foo").loadOnStartup(3))
                .append(new WarBuilder.ServletMapping(BarServlet.class).url("/bar").loadOnStartup(2))
                .append(new WarBuilder.ServletMapping(BarServlet2.class).url("/bar2").loadOnStartup(2))
                .append(new WarBuilder.ServletMapping(FooBarServlet.class).url("/foobar").loadOnStartup(1))
        ;
    }

    @Test
    public void test_init_order() throws IOException {
        Assert.assertEquals("foo_4", jettyRequest(contextPath + "/foo").get().asString());
        Assert.assertEquals("bar_2", jettyRequest(contextPath + "/bar").get().asString());
        Assert.assertEquals("bar2_3", jettyRequest(contextPath + "/bar2").get().asString());
        Assert.assertEquals("foobar_1", jettyRequest(contextPath + "/foobar").get().asString());

        Assert.assertEquals("foo_4", myRequest(contextPath + "/foo").get().asString());
        Assert.assertEquals("bar_2", myRequest(contextPath + "/bar").get().asString());
        Assert.assertEquals("bar2_3", jettyRequest(contextPath + "/bar2").get().asString());
        Assert.assertEquals("foobar_1", myRequest(contextPath + "/foobar").get().asString());
    }

    public static class FooServlet extends HttpServlet {
        protected String value;

        @Override
        public void init() throws ServletException {
            Integer counter = (Integer) getServletContext().getAttribute("counter");
            if (counter == null)
                counter = 1;
            value = getPrefix() + counter;
//            System.out.println("init " + getServletContext().getClass().getCanonicalName() + " " + getClass().getSimpleName() + ": " + value);
            getServletContext().setAttribute("counter", counter + 1);
        }

        protected String getPrefix() {
            return "foo_";
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write(value);
        }
    }

    public static class BarServlet extends FooServlet {
        @Override
        protected String getPrefix() {
            return "bar_";
        }
    }

    public static class BarServlet2 extends FooServlet {
        @Override
        protected String getPrefix() {
            return "bar2_";
        }
    }

    public static class FooBarServlet extends FooServlet {
        @Override
        protected String getPrefix() {
            return "foobar_";
        }
    }
}
