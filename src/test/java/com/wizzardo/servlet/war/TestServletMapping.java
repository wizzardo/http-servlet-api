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
 * Created by wizzardo on 25.01.15.
 */
public class TestServletMapping extends WarTest {
    @Override
    protected void customizeWar(WarBuilder builder) {
        builder.addClass(SimpleServlet.class);
        builder.addClass(EndsWithServlet.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(SimpleServlet.class).url("/simple/*"))
                .append(new WarBuilder.ServletMapping(StaticServlet.class).url("/simple"))
                .append(new WarBuilder.ServletMapping(BarServlet.class).url("/simple/bar/*"))
                .append(new WarBuilder.ServletMapping(EndsWithServlet.class).url("*.foo"))
        ;
    }


    @Test
    public void test() throws IOException {
        servletPath = "";

        Assert.assertEquals("StaticServlet", myRequest(contextPath + "/simple").get().asString());
        Assert.assertEquals("SimpleServlet", myRequest(contextPath + "/simple/foo").get().asString());
        Assert.assertEquals("BarServlet", myRequest(contextPath + "/simple/bar").get().asString());
        Assert.assertEquals("EndsWithServlet", myRequest(contextPath + "/simple.foo").get().asString());
        Assert.assertEquals("SimpleServlet", myRequest(contextPath + "/simple/1.foo").get().asString());
        Assert.assertEquals("EndsWithServlet", myRequest(contextPath + "/bar.foo").get().asString());

        test(request -> request.get().asString(), "/simple");
        test(request -> request.get().asString(), "/simple/bar");
        test(request -> request.get().asString(), "/simple.foo");
        test(request -> request.get().asString(), "/bar.foo");
    }

    public static class SimpleServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write(getClass().getSimpleName());
        }
    }

    public static class EndsWithServlet extends SimpleServlet {
    }

    public static class BarServlet extends SimpleServlet {
    }

    public static class StaticServlet extends SimpleServlet {
    }
}
