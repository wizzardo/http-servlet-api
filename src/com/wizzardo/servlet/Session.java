package com.wizzardo.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * @author: wizzardo
 * Date: 17.10.14
 */
public class Session implements HttpSession {

    private com.wizzardo.http.Session session;
    private long creationTime = System.currentTimeMillis();
    private long lastAccessedTime;

    Session(com.wizzardo.http.Session session) {
        this.session = session;
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
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
