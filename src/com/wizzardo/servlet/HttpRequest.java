package com.wizzardo.servlet;

import com.wizzardo.http.MultiValue;
import com.wizzardo.http.request.MultiPartEntry;
import com.wizzardo.http.request.Request;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: wizzardo
 * Date: 14.10.14
 */
public class HttpRequest implements HttpServletRequest {
    private static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        }
    };

    private Request request;
    private Map<String, Object> attributes;
    private Session session;
    private Context context;
    private Cookie[] cookies;

    HttpRequest(Context context, Request request) {
        this.request = request;
        this.context = context;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        if (cookies == null) {
            cookies = new Cookie[request.cookies().size()];
            int i = 0;
            for (Map.Entry<String, String> entry : request.cookies().entrySet()) {
                cookies[i++] = new Cookie(entry.getKey(), entry.getValue());
            }
        }
        return cookies;
    }

    @Override
    public long getDateHeader(String s) {
        String date = getHeader(s);
        if (date == null)
            return -1;

        try {
            Date d = dateFormatThreadLocal.get().parse(date);
            return d.getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String getHeader(String s) {
        return request.header(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return Collections.enumeration(request.headers(s));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(request.headers().keySet());
    }

    @Override
    public int getIntHeader(String s) {
        String value = request.header(s);
        if (value == null)
            return -1;
        return Integer.parseInt(value);
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
        return context.getContextPath();
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
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
        return request.path();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer sb = new StringBuffer();
        if (isSecure())
            sb.append("https://");
        else
            sb.append("http://");

        sb.append(context.getHost());
        int port = context.getPort();
        if ((!isSecure() && port != 80) || (isSecure() && port != 443))
            sb.append(":").append(port);

        sb.append(request.path());
        return sb;
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public HttpSession getSession(boolean b) {
        if (session == null) {
            session = Session.get(request.session());
            if (session != null) {
                session.updateAccessedTime();
                session.setIsNew(false);
            } else if (b)
                session = Session.create(request.session());
        }

        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
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
        if (!request.isMultipart())
            return Collections.emptyList();

        List<Part> parts = new ArrayList<Part>();

        for (MultiPartEntry entry : request.entries())
            parts.add(new MultiPart(entry));

        return parts;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return new MultiPart(request.entry(name));
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Object getAttribute(String s) {
        return attributes == null ? null : attributes.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        if (attributes == null)
            return Collections.emptyEnumeration();

        return Collections.enumeration(attributes.keySet());
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
        return (int) request.contentLength();
    }

    @Override
    public long getContentLengthLong() {
        return request.contentLength();
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
        return request.param(s);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(request.params().keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        List<String> params = request.params(s);
        if (params == null)
            return null;

        return params.toArray(new String[params.size()]);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        for (Map.Entry<String, MultiValue> entry : request.params().entrySet()) {
            params.put(entry.getKey(), entry.getValue().asArray());
        }
        return params;
    }

    @Override
    public String getProtocol() {
        return request.protocol();
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return context.getHost();
    }

    @Override
    public int getServerPort() {
        return context.getPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(request.getInputStream()));
    }

    @Override
    public String getRemoteAddr() {
        return request.connection().getIp();
    }

    @Override
    public String getRemoteHost() {
        return getRemoteAddr();
    }

    @Override
    public void setAttribute(String s, Object o) {
        if (attributes == null)
            attributes = new HashMap<String, Object>();

        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        if (attributes == null)
            return;

        attributes.remove(s);
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
        return false;
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
        return request.connection().getPort();
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

    private static class MultiPart implements Part {
        private MultiPartEntry entry;

        private MultiPart(MultiPartEntry entry) {
            this.entry = entry;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return entry.inputStream();
        }

        @Override
        public String getContentType() {
            return entry.header("Content-Disposition");
        }

        @Override
        public String getName() {
            return entry.name();
        }

        @Override
        public String getSubmittedFileName() {
            return entry.fileName();
        }

        @Override
        public long getSize() {
            return entry.length();
        }

        @Override
        public void write(String fileName) throws IOException {
            throw new UnsupportedOperationException("Not implemented yet.");
        }

        @Override
        public void delete() throws IOException {
            entry.delete();
        }

        @Override
        public String getHeader(String name) {
            return entry.header(name);
        }

        @Override
        public Collection<String> getHeaders(String name) {
            throw new UnsupportedOperationException("Not implemented yet.");
        }

        @Override
        public Collection<String> getHeaderNames() {
            return entry.headers().keySet();
        }
    }
}
