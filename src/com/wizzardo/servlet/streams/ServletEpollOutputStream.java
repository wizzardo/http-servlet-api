package com.wizzardo.servlet.streams;

import com.wizzardo.http.EpollOutputStream;
import com.wizzardo.servlet.HttpRequest;
import com.wizzardo.servlet.ServletHttpConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class ServletEpollOutputStream extends EpollOutputStream {

    protected AsyncServletOutputStream asyncServletOutputStream;

    public ServletEpollOutputStream(ServletHttpConnection connection) {
        super(connection);
        asyncServletOutputStream = new AsyncServletOutputStream(connection.getRequest());
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

    public class AsyncServletOutputStream extends ServletOutputStream {
        protected HttpRequest request;
        protected volatile WriteListenerWrapper listener;

        public AsyncServletOutputStream(HttpRequest request) {
            this.request = request;
        }

        @Override
        public boolean isReady() {
            return ServletEpollOutputStream.this.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            if (listener != null)
                throw new IllegalStateException("Listener was already set");
            listener = new WriteListenerWrapper(writeListener);
        }

        @Override
        public void write(int b) throws IOException {
            ServletEpollOutputStream.this.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            ServletEpollOutputStream.this.write(b, off, len);
        }
    }
}
