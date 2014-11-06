package com.wizzardo.servlet;

import com.wizzardo.tools.http.ConnectionMethod;
import org.junit.Test;

import javax.servlet.http.Cookie;
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


        servlet.get = (req, resp) -> resp.getWriter().write(req.getContextPath());
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(req.getRequestURL().toString().replaceAll("[0-9]+", "port"));
        test(request -> request.get().asString());
        test(request -> request.get().asString(), "foo");
        test(request -> request.param("foo", "bar").get().asString(), "foo");

        servlet.get = (req, resp) -> resp.getWriter().write(String.valueOf(req.getQueryString()));
        test(request -> request.get().asString());
        test(request -> request.param("foo", "bar").get().asString());
        test(request -> request.get().asString(), "?");

        servlet.get = (req, resp) -> resp.getWriter().write(req.getRequestURI());
        test(request -> request.get().asString());
        test(request -> request.param("foo", "bar").get().asString());
        test(request -> request.param("foo", "bar").get().asString(), "foo");

        servlet.get = (req, resp) -> resp.getWriter().write(req.getScheme());
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(req.getProtocol());
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(req.getServerName());
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> resp.getWriter().write(String.valueOf(req.getServerPort() == jettyPort || req.getServerPort() == myPort));
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> {
            String cookies;
            if (req.getCookies() == null)
                cookies = "null";
            else {
                StringBuilder sb = new StringBuilder();
                for (Cookie cookie : req.getCookies()) {
                    if (sb.length() > 0)
                        sb.append("; ");
                    sb.append(cookie.getName()).append(": ").append(cookie.getValue());
                }
                cookies = sb.toString();
            }
            resp.getWriter().write(cookies);
        };
        test(request -> request.get().asString());
        test(request -> request.header("Cookie", "").get().asString());
        test(request -> request.header("Cookie", "key=value").get().asString());
        test(request -> request.header("Cookie", "key=value;foo=bar").get().asString());
        test(request -> request.header("Cookie", "key=value; foo=bar; ikiki = ololo  ").get().asString());
    }


    @Test
    public void httpMethods() throws IOException {
        servlet.get = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.get().asString());

        servlet.post = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.post().asString());

        servlet.put = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.method(ConnectionMethod.PUT).execute().asString());

        servlet.delete = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.method(ConnectionMethod.DELETE).execute().asString());

        servlet.options = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.method(ConnectionMethod.OPTIONS).execute().asString());

        servlet.head = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.method(ConnectionMethod.HEAD).execute().asString());

        servlet.trace = (req, resp) -> resp.getWriter().write(req.getMethod());
        test(request -> request.method(ConnectionMethod.TRACE).execute().asString());
    }
}
