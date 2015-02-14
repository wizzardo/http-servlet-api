package com.wizzardo.servlet;

import com.wizzardo.http.ChainUrlMapping;
import com.wizzardo.http.UrlMapping;
import com.wizzardo.tools.misc.UncheckedThrow;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wizzardo
 * Date: 31.10.14
 */
public class Context implements ServletContext {
    private static final int MAJOR_SERVLET_VERSION = 3;
    private static final int MINOR_SERVLET_VERSION = 0;

    protected String host;
    protected int port;
    protected String contextPath;
    protected File contextDir;
    protected UrlMapping<ServletHolder> servletsMapping = new UrlMapping<>();
    protected ChainUrlMapping<Filter> filtersMapping = new ChainUrlMapping<>();
    protected List<Servlet> servletsToDestroy = new ArrayList<>();
    protected List<Filter> filtersToDestroy = new ArrayList<>();
    protected List<ServletContextListener> contextListeners = new ArrayList<>();
    protected Map<String, Object> attributes = new ConcurrentHashMap<>();
    protected Map<String, String> initParams = new ConcurrentHashMap<>();
    protected boolean initialized = false;

    public Context(String host, int port, String contextPath) {
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
    }

    void setContextDir(File contextDir) {
        this.contextDir = contextDir;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return false;
    }

    public UrlMapping<ServletHolder> getServletsMapping() {
        return servletsMapping;
    }

    public ChainUrlMapping<Filter> getFiltersMapping() {
        return filtersMapping;
    }

    public String createAbsoluteUrl(String path) {
        StringBuilder sb = new StringBuilder();
        if (isSecure())
            sb.append("https://");
        else
            sb.append("http://");

        sb.append(host);
        if ((!isSecure() && port != 80) || (isSecure() && port != 443))
            sb.append(":").append(port);

        sb.append(path);
        return sb.toString();
    }

    protected void onInit() {
        ServletContextEvent event = new ServletContextEvent(this);
        for (ServletContextListener listener : contextListeners)
            listener.contextInitialized(event);
        initialized = true;
    }

    protected void addServletToDestroy(Servlet servlet) {
        servletsToDestroy.add(servlet);
    }

    protected void addFilterToDestroy(Filter filter) {
        filtersToDestroy.add(filter);
    }

    protected void onDestroy() {
        for (Servlet servlet : servletsToDestroy)
            servlet.destroy();

        for (Filter filter : filtersToDestroy)
            filter.destroy();

        ServletContextEvent event = new ServletContextEvent(this);
        for (int i = contextListeners.size() - 1; i >= 0; i--)
            contextListeners.get(i).contextDestroyed(event);
    }

    @Override
    public ServletContext getContext(String uripath) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_SERVLET_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_SERVLET_VERSION;
    }

    @Override
    public int getEffectiveMajorVersion() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getEffectiveMinorVersion() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getMimeType(String file) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        try {
            return new FileInputStream(new File(contextDir, path));
        } catch (FileNotFoundException e) {
            throw UncheckedThrow.rethrow(e);
        }
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return new ServletRequestDispatcher(servletsMapping.get(path), path);
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return Collections.emptyEnumeration();
    }

    @Override
    public Enumeration<String> getServletNames() {
        return Collections.emptyEnumeration();
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        System.out.println(msg);
        exception.printStackTrace();
    }

    @Override
    public void log(String message, Throwable throwable) {
        System.out.println(message);
        throwable.printStackTrace();
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getServerInfo() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParams.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        if (initialized)
            throw new IllegalStateException("this ServletContext has already been initialized");

        return initParams.putIfAbsent(name, value) != null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object object) {
        if (object == null)
            removeAttribute(name);
        else
            attributes.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public String getServletContextName() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void addListener(String className) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void addContextListener(ServletContextListener listener) {
        contextListeners.add(listener);
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void declareRoles(String... roleNames) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getVirtualServerName() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
