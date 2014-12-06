package com.wizzardo.servlet.streams;

import com.wizzardo.epoll.Connection;
import com.wizzardo.http.EpollInputStream;

import java.io.IOException;

/**
* @author: wizzardo
* Date: 05.11.14
*/
public class ServletEpollInputStream extends EpollInputStream {

    protected AsyncServletInputStream asyncServletInputStream;

    public ServletEpollInputStream(Connection connection, byte[] buffer, int currentOffset, int currentLimit, long contentLength) {
        super(connection, buffer, currentOffset, currentLimit, contentLength);
    }

    @Override
    protected void fillBuffer() throws IOException {
        super.fillBuffer();
    }

    @Override
    protected void waitForData() throws IOException {
        if (!asyncServletInputStream.request.isAsyncStarted())
            super.waitForData();
    }

    @Override
    protected void wakeUp() {
        if (!asyncServletInputStream.request.isAsyncStarted())
            super.wakeUp();
        else if (asyncServletInputStream.listener != null)
            asyncServletInputStream.listener.onDataAvailable();
    }
}
