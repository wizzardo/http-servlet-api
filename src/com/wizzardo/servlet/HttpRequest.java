package com.wizzardo.servlet;

import com.wizzardo.http.request.Request;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * @author: wizzardo
 * Date: 14.10.14
 */
public class HttpRequest implements HttpServletRequest {
    private Request request;

    HttpRequest(Request request) {
        this.request = request;
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public long getDateHeader(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getHeader(String s) {
        return request.header(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(request.headers().keySet());
    }

    @Override
    public int getIntHeader(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isUserInRole(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public HttpSession getSession(boolean b) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Object getAttribute(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getParameter(String s) {
//        return request.param(s);
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Enumeration<String> getParameterNames() {
//        return Collections.enumeration(request.params().keySet());
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String[] getParameterValues(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setAttribute(String s, Object o) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void removeAttribute(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRealPath(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
