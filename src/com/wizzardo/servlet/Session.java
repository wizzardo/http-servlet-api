package com.wizzardo.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wizzardo
 * Date: 17.10.14
 */
public class Session implements HttpSession {

    private ServletContext servletContext;
    private com.wizzardo.http.Session session;
    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
    private boolean isNew = true;
    private long creationTime = System.currentTimeMillis();
    private long lastAccessedTime = creationTime;

    private Session(com.wizzardo.http.Session session, ServletContext context) {
        this.session = session;
        this.servletContext = context;
        session.put("session", this);
    }

    static Session create(com.wizzardo.http.Session session, ServletContext context) {
        return new Session(session, context);
    }

    static Session get(com.wizzardo.http.Session session) {
        return (Session) session.get("session");
    }

    void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    void updateAccessedTime() {
        lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setTTL(interval * 1000l);
    }

    @Override
    public int getMaxInactiveInterval() {
        return (int) (session.getTTL() / 1000);
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return new HttpSessionContext() {
            @Override
            public HttpSession getSession(String sessionId) {
                return null;
            }

            @Override
            public Enumeration<String> getIds() {
                return Collections.emptyEnumeration();
            }
        };
    }

    @Override
    public Object getAttribute(String name) {
        return map.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(map.keySet());
    }

    @Override
    public String[] getValueNames() {
        return map.keySet().toArray(new String[map.size()]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        map.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        map.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        map.clear();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
