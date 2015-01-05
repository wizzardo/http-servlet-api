package com.wizzardo.servlet;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 06.12.14
 */
public abstract class WarTest extends ServerTest {

    @Override
    protected void init() throws IOException {
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
            builder.addClass(OkServlet.class);
            builder.getWebXmlBuilder()
                    .append(new WarBuilder.ServletMapping(OkServlet.class).url("/ok"));
        }

        @Test
        public void testOk() throws IOException {
            Assert.assertEquals("ok", jettyRequest("/http/ok").get().asString());
            Assert.assertEquals("ok", myRequest("/http/ok").get().asString());
        }

        public static class OkServlet extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentLength(2);
                resp.getWriter().write("ok");
            }
        }
    }

}
