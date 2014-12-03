package com.wizzardo.servlet;

import com.wizzardo.http.request.Header;
import com.wizzardo.http.response.CookieBuilder;
import com.wizzardo.http.response.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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

    boolean hasWriter() {
        return writer != null;
    }

    byte[] getData() {
        if (writer == null)
            return null;

        writer.flush();
        return buffer.toByteArray();
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
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void sendError(int sc) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
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
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null)
            writer = new PrintWriter(buffer = new ByteArrayOutputStream());

        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new UnsupportedOperationException("Not implemented yet.");
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
        header(Header.KEY_CONTENT_TYPE, type);
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not implemented yet.");
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
