package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wizzardo on 22.01.15.
 */
public class TestFilter extends WarTest {

    @Override
    protected void customizeWar(WarBuilder builder) {
        servletPath = "/filtered";
        builder.addClass(SimpleServlet.class);
        builder.addClass(SimpleFilter.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(SimpleServlet.class).url("/filtered"))
                .append(new WarBuilder.FilterMapping(SimpleFilter.class).url("/*"))
        ;
    }

    @Test
    public void test() throws IOException {
        test(request -> request.get().asString());
        Assert.assertEquals("<response>ok</response>", myRequest().get().asString());
    }

    public static class SimpleServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("ok");
        }
    }

    public static class SimpleFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            response.getWriter().write("<response>");
            chain.doFilter(request, response);
            response.getWriter().write("</response>");
        }

        @Override
        public void destroy() {
        }
    }
}

