package com.wizzardo.servlet;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 23.10.14
 */
public class CustomServlet extends HttpServlet {

    volatile Handler get;
    volatile Handler head;
    volatile Handler post;
    volatile Handler put;
    volatile Handler delete;
    volatile Handler options;
    volatile Handler trace;

    public void clean() {
        get = null;
        head = null;
        post = null;
        put = null;
        delete = null;
        options = null;
        trace = null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (get == null)
            super.doGet(req, resp);
        else
            get.handle(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (head == null)
            super.doHead(req, resp);
        else
            head.handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (post == null)
            super.doPost(req, resp);
        else
            post.handle(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (put == null)
            super.doPut(req, resp);
        else
            put.handle(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (delete == null)
            super.doDelete(req, resp);
        else
            delete.handle(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (options == null)
            super.doOptions(req, resp);
        else
            options.handle(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (trace == null)
            super.doTrace(req, resp);
        else
            trace.handle(req, resp);
    }

    public static interface Handler {
        void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
    }
}
