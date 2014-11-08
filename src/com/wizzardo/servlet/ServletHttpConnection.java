package com.wizzardo.servlet;

import com.wizzardo.http.HttpConnection;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
class ServletHttpConnection extends HttpConnection<HttpRequest, HttpResponse> {

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
}
