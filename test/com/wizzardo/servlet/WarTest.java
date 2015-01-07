package com.wizzardo.servlet;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: wizzardo
 * Date: 06.12.14
 */
public abstract class WarTest extends ServerTest {

    @Override
    protected void init() throws IOException {
        contextPath = "/http";
        WarBuilder builder = new WarBuilder();
        customizeWar(builder);
        File war = builder.build("/tmp/http.war");
        war.deleteOnExit();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/http");
        webapp.setWar(war.getAbsolutePath());

        jetty.setHandler(webapp);

        myServer.registerWar(war.getAbsolutePath());
    }

    protected abstract void customizeWar(WarBuilder warBuilder);

    public static class TestOk extends WarTest {

        @Override
        protected void customizeWar(WarBuilder builder) {
            servletPath = "/ok";
            builder.addClass(OkServlet.class);
            builder.getWebXmlBuilder()
                    .append(new WarBuilder.ServletMapping(OkServlet.class).url("/ok"));
        }

        @Test
        public void testOk() throws IOException {
            Assert.assertEquals("ok", jettyRequest().get().asString());
            Assert.assertEquals("ok", myRequest().get().asString());
        }

        public static class OkServlet extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentLength(2);
                resp.getWriter().write("ok");
            }
        }
    }

    public static class TestContextParams extends WarTest {

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

    public static class TestServletParams extends WarTest {

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

}
