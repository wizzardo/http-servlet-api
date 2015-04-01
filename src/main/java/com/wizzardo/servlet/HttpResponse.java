package com.wizzardo.servlet;

import com.wizzardo.epoll.readable.ReadableData;
import com.wizzardo.http.mapping.Path;
import com.wizzardo.http.request.Header;
import com.wizzardo.http.response.CookieBuilder;
import com.wizzardo.http.response.Response;
import com.wizzardo.http.response.Status;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author: wizzardo
 * Date: 26.10.14
 */
public class HttpResponse extends Response implements HttpServletResponse {

    protected static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            return format;
        }
    };

    private ByteArrayOutputStream buffer;
    private PrintWriter writer;
    private int status = 200;
    private String statusMessage;
    private Context context;
    private HttpRequest request;

    protected String contentType;
    protected String charset;

    boolean hasWriter() {
        return writer != null;
    }

    byte[] getData() {
        if (writer == null)
            return null;

        writer.flush();
        byte[] bytes = buffer.toByteArray();
        buffer.reset();
        return bytes;
    }

    void setContext(Context context) {
        this.context = context;
    }

    void setRequest(HttpRequest request) {
        this.request = request;
    }

    @Override
    public void addCookie(Cookie cookie) {
        CookieBuilder cb = new CookieBuilder(cookie.getName(), cookie.getValue());
        if (cookie.getMaxAge() > 0)
            cb.expires(System.currentTimeMillis() + 1000 * cookie.getMaxAge());
        else if (cookie.getMaxAge() == 0)
            cb.expires(0);

        if (cookie.getPath() != null)
            cb.path(cookie.getPath());
        if (cookie.getDomain() != null)
            cb.domain(cookie.getDomain());
        if (cookie.isHttpOnly())
            cb.httpOnly();
        if (cookie.getSecure())
            cb.secure();

        String c = cb.build();
        if (cookie.getComment() != null)
            cookie.setVersion(1);

        if (cookie.getVersion() != 0) {
            StringBuilder sb = new StringBuilder(c);
            sb.append(";Version=").append(cookie.getVersion());

            if (cookie.getComment() != null)
                sb.append(";Comment=").append(cookie.getComment());

            c = sb.toString();
        }
        setCookie(c);
    }

    @Override
    public String encodeURL(String url) {
        return url;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url;
    }

    @Override
    public String encodeUrl(String url) {
        return url;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return url;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        if (isCommitted())
            throw new IllegalStateException("the response has already been committed");

        setHeader(Header.KEY_CONTENT_TYPE, Header.VALUE_HTML_UTF8);
        setBody(msg); //todo: render custom error page
        status = sc;

        commit(request.connection());
    }

    @Override
    public void sendError(int sc) throws IOException {
        sendError(sc, "Error: " + sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        if (isCommitted())
            throw new IllegalStateException("the response has already been committed");

        if (location.startsWith("/"))
            location = context.createAbsoluteUrl(location);
        else if (!location.startsWith("http://") && !location.startsWith("https://")) {
            Path path = request.path();
            if (!path.isEndsWithSlash())
                path = path.subPath(0, path.length() - 1);
            path = path.add(location);
            location = context.createAbsoluteUrl(path.toString());
        }

        setRedirectTemporarily(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        header(name, dateFormatThreadLocal.get().format(new Date(date)));
    }

    @Override
    public void addDateHeader(String name, long date) {
        appendHeader(name, dateFormatThreadLocal.get().format(new Date(date)));
    }

    @Override
    public void addHeader(String name, String value) {
        appendHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        header(name, String.valueOf(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        appendHeader(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        status = sc;
        statusMessage = sm;
    }

    @Override
    public Response setStatus(Status status) {
        setStatus(status.code, status.message);
        return this;
    }

    @Override
    protected byte[] statusToBytes() {
        if (statusMessage == null)
            return ("HTTP/1.1 " + status + "\r\n").getBytes();
        else
            return ("HTTP/1.1 " + status + " " + statusMessage + "\r\n").getBytes();
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        return header(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headerNames();
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public String getContentType() {
        if (charset != null && contentType != null)
            return contentType + "; charset=" + charset;

        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return getOutputStream(request.connection()).getServletOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null)
            writer = new PrintWriter(new OutputStreamWriter(provideOut(), charset != null ? Charset.forName(charset) : StandardCharsets.UTF_8), true);

        return writer;
    }

    protected OutputStream provideOut() throws IOException {
        if (request.isAsyncStarted())
            return getOutputStream();

        if (buffer == null)
            buffer = new ByteArrayOutputStream();
        return buffer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public void setContentLength(int len) {
        header(Header.KEY_CONTENT_LENGTH, len);
    }

    @Override
    public void setContentLengthLong(long len) {
        header(Header.KEY_CONTENT_LENGTH, len);
    }

    @Override
    public void setContentType(String type) {
        int i = type.indexOf("charset=");
        if (i == -1) {
            contentType = type;
        } else {
            charset = type.substring(i + 8);
            contentType = type.substring(0, type.lastIndexOf(";", i));
        }
    }

    @Override
    public void setBufferSize(int size) {
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (!isCommitted())
            commit(request.connection());
    }

    @Override
    public void resetBuffer() {
        if (isCommitted())
            throw new IllegalStateException("the response has already been committed");
    }

    @Override
    public void reset() {
        if (isCommitted())
            throw new IllegalStateException("the response has already been committed");
        if (buffer == null)
            return;

        buffer.reset();
        writer = null;
    }

    @Override
    public ReadableData toReadableBytes() {
        if (contentType != null && charset != null)
            header(Header.KEY_CONTENT_TYPE, contentType + "; charset=" + charset);
        else if (contentType != null)
            header(Header.KEY_CONTENT_TYPE, contentType);

        return super.toReadableBytes();
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
