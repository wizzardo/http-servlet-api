package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
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
                .append(new WarBuilder.ServletMapping(EndsWithServlet.class).url("*.foo"))
        ;
    }


    @Test
    public void test() throws IOException {
        servletPath = "";
        test(request -> request.get().asString(), "/simple");
        test(request -> request.get().asString(), "/simple.foo");
        test(request -> request.get().asString(), "/bar.foo");
    }

    public static class SimpleServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write(getClass().getSimpleName());
        }
    }

    public static class EndsWithServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write(getClass().getSimpleName());
        }
    }
}
