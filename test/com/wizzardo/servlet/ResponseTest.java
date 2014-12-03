package com.wizzardo.servlet;

import org.junit.Test;

import javax.servlet.http.Cookie;
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

    @Test
    public void cookies() throws IOException {
        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setMaxAge(30);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setPath("/path");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setDomain(".domain.com");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setHttpOnly(true);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setSecure(true);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setComment("comment");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setVersion(1);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie"));
    }
}
