package com.wizzardo.servlet;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 23.10.14
 */
public class RequestTest extends ServerTest {
    @Test
    public void ok() throws IOException {
        servlet.get = (req, resp) -> {
            resp.getWriter().write("ok");
        };

        Assert.assertEquals("ok", jettyRequest("/servlet").get().asString());
    }
}
