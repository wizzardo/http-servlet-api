package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wizzardo on 22.01.15.
 */
public class TestServletParams extends WarTest {

    @Override
    protected void customizeWar(WarBuilder builder) {
        servletPath = "/params";
        builder.addClass(ParamServlet.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(ParamServlet.class)
                                .url("/params")
                                .param("foo", "bar")
                                .param("key", "value")
                );
    }

    @Test
    public void testOk() throws IOException {
        test(request -> request.get().asString());
        test(request -> request.param("param", "key").get().asString());
        test(request -> request.param("param", "foo").get().asString());
    }

    public static class ParamServlet extends HttpServlet {

        boolean initialized = false;

        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            Assert.assertEquals("ParamServlet", config.getServletName());

            Map<String, String> initParams = new LinkedHashMap<>();
            Enumeration<String> params = config.getInitParameterNames();
            while (params.hasMoreElements()) {
                String param = params.nextElement();
                initParams.put(param, config.getInitParameter(param));
            }

            Assert.assertEquals(2, initParams.size());
            Assert.assertEquals("bar", initParams.get("foo"));
            Assert.assertEquals("value", initParams.get("key"));
            initialized = true;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            Assert.assertTrue(initialized);

            String param = req.getParameter("param");
            if (param == null) {
                int i = 0;
                Enumeration<String> params = getInitParameterNames();
                while (params.hasMoreElements()) {
                    params.nextElement();
                    i++;
                }
                resp.getWriter().write("params: " + i);
                return;
            }

            resp.getWriter().write(param + ": " + getInitParameter(param));
        }
    }
}

