package com.wizzardo.servlet.wartests;

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
 * Created by wizzardo on 22.01.15.
 */
public class TestOk extends WarTest {

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
