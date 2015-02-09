package com.wizzardo.servlet;

import com.wizzardo.http.MultiValue;
import com.wizzardo.http.Path;
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
public class HttpRequest extends Request<ServletHttpConnection> implements HttpServletRequest {
    private static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        }
    };

    private Map<String, Object> attributes;
    private Session session;
    private Context context;
    private Cookie[] cookies;
    private boolean isAsyncStarted = false;

    protected Path servletPath;

    public HttpRequest(ServletHttpConnection connection) {
        super(connection);
    }

    void setContext(Context context) {
        this.context = context;
    }

    void setServletPath(Path path) {
        this.servletPath = path;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        if (cookies == null) {
            cookies = new Cookie[cookies().size()];
            int i = 0;
            for (Map.Entry<String, String> entry : cookies().entrySet()) {
                cookies[i++] = new Cookie(entry.getKey(), entry.getValue());
            }
        }
        if (cookies.length == 0)
            return null;

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
        return header(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return Collections.enumeration(headers(s));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers().keySet());
    }

    @Override
    public int getIntHeader(String s) {
        String value = header(s);
        if (value == null)
            return -1;
        return Integer.parseInt(value);
    }

    @Override
    public String getMethod() {
        return method().name();
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
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isUserInRole(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getRequestURI() {
        return path().toString();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(context.createAbsoluteUrl(path().toString()));
    }

    @Override
    public String getServletPath() {
        return servletPath.toString();
    }

    @Override
    public HttpSession getSession(boolean b) {
        if (session == null) {
            session = Session.get(session(context.getContextPath()));
            if (session != null) {
                session.updateAccessedTime();
                session.setIsNew(false);
            } else if (b)
                session = Session.create(session(context.getContextPath()), context);
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
        if (!isMultipart())
            return Collections.emptyList();

        List<Part> parts = new ArrayList<Part>();

        for (MultiPartEntry entry : entries())
            parts.add(new MultiPart(entry));

        return parts;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return new MultiPart(entry(name));
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
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getContentLength() {
        return (int) contentLength();
    }

    @Override
    public long getContentLengthLong() {
        return contentLength();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletInputStream getInputStream() {
        return connection().getInputStream().getServletInputStream();
    }

    @Override
    public String getParameter(String s) {
        return param(s);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params().keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        List<String> params = params(s);
        if (params == null)
            return null;

        return params.toArray(new String[params.size()]);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        for (Map.Entry<String, MultiValue> entry : params().entrySet()) {
            params.put(entry.getKey(), entry.getValue().asArray());
        }
        return params;
    }

    @Override
    public String getProtocol() {
        return protocol();
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
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getRemoteAddr() {
        return connection().getIp();
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
        return context.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return context.getRequestDispatcher(s);
    }

    @Override
    public String getRealPath(String s) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getRemotePort() {
        return connection().getPort();
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
        return isAsyncStarted;
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
