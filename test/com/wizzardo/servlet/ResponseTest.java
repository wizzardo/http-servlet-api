package com.wizzardo.servlet;

import org.junit.Test;

import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 09.11.14
 */
public class ResponseTest extends ServerTest {

    @Test
    public void headers() throws IOException {
        servlet.get = (req, resp) -> {
            resp.setHeader("Connection", "close");
            resp.setHeader("Date", "now");
            resp.addHeader("foo", "value_1");
            resp.addHeader("foo", "value_2");
            resp.getWriter().write(resp.getHeaderNames().toString());
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Connection"));
        test(request -> request.get().header("Date"));
        test(request -> request.get().header("foo"));
        test(request -> request.get().headers().get("foo"));
    }
}
