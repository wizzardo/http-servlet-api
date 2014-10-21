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

    private com.wizzardo.http.Session session;
    private ConcurrentHashMap<String, Object> map;
    private boolean newSession = false;
    private long creationTime = System.currentTimeMillis();
    private long lastAccessedTime;

    Session(com.wizzardo.http.Session session) {
        this.session = session;
        map = (ConcurrentHashMap<String, Object>) session.get("data");
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            newSession = true;
            session.put("data", map);
        }
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
        throw new UnsupportedOperationException("Not implemented yet.");
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
        throw new UnsupportedOperationException("Not implemented yet.");
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
        return newSession;
    }
}
