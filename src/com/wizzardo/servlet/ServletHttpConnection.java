package com.wizzardo.servlet;

import com.wizzardo.http.EpollOutputStream;
import com.wizzardo.http.HttpConnection;
import com.wizzardo.servlet.streams.ServletEpollInputStream;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class ServletHttpConnection extends HttpConnection<HttpRequest, HttpResponse, ServletEpollInputStream, EpollOutputStream> {

    public ServletHttpConnection(int fd, int ip, int port) {
        super(fd, ip, port);
    }

    @Override
    protected HttpRequest createRequest() {
        return new HttpRequest(this);
    }

    @Override
    protected HttpResponse createResponse() {
        return new HttpResponse();
    }

    @Override
    protected ServletEpollInputStream createInputStream(byte[] buffer, int currentOffset, int currentLimit, long contentLength) {
        return new ServletEpollInputStream(this, buffer, currentOffset, currentLimit, contentLength);
    }
}
