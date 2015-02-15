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
        builder.addClass(ServletForward.class);
        builder.addClass(Servlet1.class);
        builder.addClass(Servlet2.class);
        builder.addClass(ServletInclude.class);
        builder.addClass(Servlet3.class);
        builder.addClass(Servlet4.class);
        builder.addClass(ServletIncludeByName.class);
        builder.addClass(ServletForwardByName.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(ServletForward.class).url("/test_forwarded"))
                .append(new WarBuilder.ServletMapping(Servlet1.class).url("/forward"))
                .append(new WarBuilder.ServletMapping(Servlet2.class).url("/forward_by_name"))
                .append(new WarBuilder.ServletMapping(ServletInclude.class).url("/test_included"))
                .append(new WarBuilder.ServletMapping(Servlet3.class).url("/include"))
                .append(new WarBuilder.ServletMapping(Servlet4.class).url("/include_by_name"))
                .append(new WarBuilder.ServletMapping(ServletIncludeByName.class).url("/test_included_name"))
                .append(new WarBuilder.ServletMapping(ServletForwardByName.class).url("/test_forwarded_name"))
        ;
    }

    @Test
    public void test_dispatcher() throws IOException {
        servletPath = "/test_forwarded";
        Assert.assertEquals("forwarded", jettyRequest().get().asString());
        Assert.assertEquals("forwarded", myRequest().get().asString());

        servletPath = "/test_forwarded_name";
        Assert.assertEquals("forwarded", jettyRequest().get().asString());
        Assert.assertEquals("forwarded", myRequest().get().asString());


        servletPath = "/test_included";
        Assert.assertEquals("<b>included</b>", jettyRequest().get().asString());
        Assert.assertEquals("<b>included</b>", myRequest().get().asString());

        servletPath = "/test_included_name";
        Assert.assertEquals("<b>included</b>", jettyRequest().get().asString());
        Assert.assertEquals("<b>included</b>", myRequest().get().asString());
    }

    public static class ServletForward extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/forward");
            requestDispatcher.forward(req, resp);
        }
    }

    public static class Servlet1 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("forwarded");
            Assert.assertEquals("/forward", req.getServletPath());
        }
    }

    public static class Servlet2 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("forwarded");
            Assert.assertEquals("/test_forwarded_name", req.getServletPath());
        }
    }

    public static class ServletInclude extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/include");
            resp.getWriter().write("<b>");
            requestDispatcher.include(req, resp);
            resp.getWriter().write("</b>");
        }
    }

    public static class Servlet3 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("included");
            Assert.assertEquals("/test_included", req.getServletPath());
        }
    }

    public static class Servlet4 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("included");
            Assert.assertEquals("/test_included_name", req.getServletPath());
        }
    }

    public static class ServletForwardByName extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            RequestDispatcher requestDispatcher = getServletContext().getNamedDispatcher("Servlet2");
            requestDispatcher.forward(req, resp);
        }
    }

    public static class ServletIncludeByName extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            RequestDispatcher requestDispatcher = getServletContext().getNamedDispatcher("Servlet4");
            resp.getWriter().write("<b>");
            requestDispatcher.include(req, resp);
            resp.getWriter().write("</b>");
        }
    }
}
