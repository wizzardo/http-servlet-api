package com.wizzardo.servlet;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 06.12.14
 */
public class WarTest extends ServerTest {

    @Override
    protected void init() {
        String warFile = "/home/wizzardo/IdeaProjects/httpTester/out/http.war";
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/http");
        webapp.setWar(warFile);

        jetty.setHandler(webapp);

        myServer.registerWar(warFile);
    }

    @Test
    public void testOk() throws IOException {
        Assert.assertEquals("ok", jettyRequest("/http/ok").get().asString());
        Assert.assertEquals("ok", myRequest("/http/ok").get().asString());
    }
}
