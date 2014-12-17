package com.wizzardo.servlet.streams;

import com.wizzardo.http.EpollOutputStream;
import com.wizzardo.servlet.ServletHttpConnection;

import javax.servlet.ServletOutputStream;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class ServletEpollOutputStream extends EpollOutputStream {

    protected AsyncServletOutputStream asyncServletOutputStream;

    public ServletEpollOutputStream(ServletHttpConnection connection) {
        super(connection);
        asyncServletOutputStream = new AsyncServletOutputStream(connection.getRequest(), this);
    }

    @Override
    protected void waitFor() {
        if (!asyncServletOutputStream.request.isAsyncStarted())
            super.waitFor();
    }

    @Override
    protected void wakeUp() {
        if (!asyncServletOutputStream.request.isAsyncStarted())
            super.wakeUp();
        else if (asyncServletOutputStream.listener != null)
            asyncServletOutputStream.listener.onWritePossible();
    }

    public ServletOutputStream getServletOutputStream() {
        return asyncServletOutputStream;
    }

    public boolean isReady() {
        return !waiting;
    }
}
