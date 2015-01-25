package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by wizzardo on 22.01.15.
 */
public class TestContextParams extends WarTest {

    @Override
    protected void customizeWar(WarBuilder builder) {
        servletPath = "/context";
        builder.addClass(ContextParamServlet.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(ContextParamServlet.class).url(servletPath))
                .param("foo", "bar")
                .param("key", "value");
    }

    @Test
    public void testOk() throws IOException {
        test(request -> request.get().asString());
        test(request -> request.param("param", "key").get().asString());
        test(request -> request.param("param", "foo").get().asString());
    }

    public static class ContextParamServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            ServletContext context = req.getSession().getServletContext();
            String param = req.getParameter("param");
            if (param == null) {
                int i = 0;
                Enumeration<String> params = context.getInitParameterNames();
                while (params.hasMoreElements()) {
                    params.nextElement();
                    i++;
                }
                resp.getWriter().write("params: " + i);
                return;
            }

            resp.getWriter().write(param + ": " + context.getInitParameter(param));
        }
    }
}
