package com.wizzardo.servlet;

import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by wizzardo on 03.01.15.
 */
public class SessionTest extends ServerTest {

    @Test
    public void simple_test() throws IOException, InterruptedException {
        testGet((req, resp) -> {
            HttpSession session = req.getSession(false);
            resp.getWriter().write("session is null: " + (session == null));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession(true);
            resp.getWriter().write("session is new: " + session.isNew());
        }, 2);

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getAttributeNames: " + enumerationToString(session.getAttributeNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getValueNames: " + Arrays.toString(session.getValueNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            Integer value = (Integer) session.getAttribute("counter");
            if (value == null)
                value = 0;

            value++;
            session.setAttribute("counter", value);
            resp.getWriter().write("value: " + value);
        }, 2);

        servlet.get = (req, resp) -> {
            resp.getWriter().write(req.getSession().getId());
        };
        String jettySession = jettyRequest().get().asString();
        String mySession = myRequest().get().asString();

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            session.invalidate();
            resp.getWriter().write("session invalidated");
        });

        servlet.get = (req, resp) -> {
            resp.getWriter().write(req.getSession().getId());
        };

        Assert.assertNotSame(jettySession, jettyRequest().get().asString());
        Assert.assertNotSame(mySession, myRequest().get().asString());

        testGet((req, resp) -> {
            HttpSession session = req.getSession(true);
            resp.getWriter().write("session is new: " + session.isNew());
        }, 2);

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            Integer value = (Integer) session.getValue("counter");
            if (value == null)
                value = 0;

            value++;
            session.putValue("counter", value);
            resp.getWriter().write("value: " + value);
        }, 2);

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getAttributeNames: " + enumerationToString(session.getAttributeNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getValueNames: " + Arrays.toString(session.getValueNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getCreationTime: " + (session.getCreationTime() / 1000));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getLastAccessedTime: " + (session.getLastAccessedTime() / 1000));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getSessionContext: " + (session.getSessionContext().getSession(null) + " and " + enumerationToString(session.getSessionContext().getIds())));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            session.removeAttribute("value");
            resp.getWriter().write("getAttributeNames: " + enumerationToString(session.getAttributeNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            session.setAttribute("value", "foobar");
            resp.getWriter().write("getAttributeNames: " + enumerationToString(session.getAttributeNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            session.removeValue("value");
            resp.getWriter().write("getAttributeNames: " + enumerationToString(session.getAttributeNames()));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            resp.getWriter().write("getContextPath: " + session.getServletContext().getContextPath());
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession();
            session.setMaxInactiveInterval(1);
            resp.getWriter().write("getMaxInactiveInterval: " + session.getMaxInactiveInterval());
        });

        Thread.sleep(1500);

        testGet((req, resp) -> {
            HttpSession session = req.getSession(false);
            resp.getWriter().write("session is null: " + (session == null));
        });

        testGet((req, resp) -> {
            HttpSession session = req.getSession(true);
            resp.getWriter().write("session is new: " + session.isNew());
        }, 2);
    }
}
