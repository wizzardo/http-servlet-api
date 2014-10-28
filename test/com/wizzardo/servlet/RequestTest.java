package com.wizzardo.servlet;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

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

        test(request -> request.get().asString());
    }

    @Test
    public void requestMethods() throws IOException {
        servlet.get = (req, resp) -> resp.getWriter().write(req.getParameter("foo"));
        test(request -> request.param("foo", "bar").get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(String.valueOf(req.getAuthType()));
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(Arrays.toString(req.getCookies()));
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> {
            try {
                resp.getWriter().write(String.valueOf(req.getDateHeader("some_date")));
            } catch (IllegalArgumentException e) {
                resp.getWriter().write("illegal date: " + String.valueOf(req.getHeader("some_date")));
            }
        };
        test(request -> request.get().asString());
        test(request -> request.header("some_date", "Tue, 15 Nov 1994 12:45:26 GMT").get().asString());
        test(request -> request.header("some_date", "exception").get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(String.valueOf(req.getHeader("custom_header")));
        test(request -> request.get().asString());
        test(request -> request.header("custom_header", "value").get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(String.valueOf(enumerationToString(req.getHeaderNames()).length()));
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(enumerationToString(req.getHeaders("User-Agent")));
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> {
            try {
                resp.getWriter().write(String.valueOf(req.getIntHeader("int_value")));
            } catch (NumberFormatException e) {
                resp.getWriter().write("NumberFormatException: " + req.getHeader("int_value"));
            }
        };
        test(request -> request.get().asString());
        test(request -> request.header("int_value", "123").get().asString());
        test(request -> request.header("int_value", "NaN").get().asString());
    }


}
