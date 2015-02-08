package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wizzardo on 08.02.15.
 */
public class TestDispatcher extends WarTest {
    @Override
    protected void customizeWar(WarBuilder builder) {
        servletPath = "/test";
        builder.addClass(Servlet1.class);
        builder.addClass(Servlet2.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(Servlet1.class).url("/test"))
                .append(new WarBuilder.ServletMapping(Servlet2.class).url("/forward"));
    }

    @Test
    public void testOk() throws IOException {
        Assert.assertEquals("forwarded", jettyRequest().get().asString());
        Assert.assertEquals("forwarded", myRequest().get().asString());
    }

    public static class Servlet1 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/forward");
            requestDispatcher.forward(req, resp);
        }
    }

    public static class Servlet2 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("forwarded");
        }
    }
}
