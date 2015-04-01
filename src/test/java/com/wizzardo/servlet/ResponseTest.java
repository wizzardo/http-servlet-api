package com.wizzardo.servlet;

import com.wizzardo.tools.security.MD5;
import org.junit.Test;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

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
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setPath("/path");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setDomain(".domain.com");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setHttpOnly(true);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setSecure(true);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setComment("comment");
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));

        servlet.get = (req, resp) -> {
            Cookie cookie = new Cookie("foo", "bar");
            cookie.setVersion(1);
            resp.addCookie(cookie);
            resp.getWriter().write("ok");
        };
        test(request -> request.get().asString());
        test(request -> request.get().header("Set-Cookie").replace('-', ' '));
    }

    @Test
    public void redirect() throws IOException {
        servlet.get = (req, resp) -> resp.sendRedirect("/redirect_here");
        test(request -> request.disableRedirects().get().header("Location").replaceAll("[0-9]+", "port"), "path");

        servlet.get = (req, resp) -> resp.sendRedirect("redirect_here");
        test(request -> request.disableRedirects().get().header("Location").replaceAll("[0-9]+", "port"), "path");

        servlet.get = (req, resp) -> resp.sendRedirect("");
        test(request -> request.disableRedirects().get().header("Location").replaceAll("[0-9]+", "port"), "path");

        servlet.get = (req, resp) -> resp.sendRedirect("http://example.com");
        test(request -> request.disableRedirects().get().header("Location").replaceAll("[0-9]+", "port"), "path");
    }

    @Test
    public void outputStream() throws IOException, InterruptedException {
        servlet.get = (req, resp) -> {
            byte[] bytes = "some data".getBytes();
            resp.setContentLength(bytes.length);
            OutputStream out = resp.getOutputStream();
            out.write(bytes);
        };
        test(request -> request.get().asString());
        test(request -> request.get().asString());
        test(request -> request.get().asString());

        byte[] data = new byte[50 * 1024 * 1024];
        new Random().nextBytes(data);
        servlet.get = (req, resp) -> {
            resp.setContentLength(data.length);
            OutputStream out = resp.getOutputStream();
            out.write(data);
        };
        test(request -> MD5.getMD5AsString(request.get().asBytes()));
        test(request -> MD5.getMD5AsString(request.get().asBytes()));
        test(request -> MD5.getMD5AsString(request.get().asBytes()));


        servlet.get = (req, resp) -> {
            byte[] bytes = "some data".getBytes();
            resp.setHeader("Connection", "Close");
            resp.setContentLength(bytes.length);
            OutputStream out = resp.getOutputStream();
            out.write(bytes);
        };
        test(request -> request.get().asString());
        //last request must be with "Connection: close"
    }

    @Test
    public void charset() throws IOException {
        servlet.get = (req, resp) -> {
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("cp1251");
            resp.getWriter().write("тест");
        };
        test(request -> request.get().asString());

        servlet.get = (req, resp) -> {
            resp.setContentType("text/plain; charset=cp1251");
            resp.getWriter().write("тест");
        };
        test(request -> request.get().asString());
    }
}
