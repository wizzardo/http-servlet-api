package com.wizzardo.servlet.streams;

import com.wizzardo.http.EpollInputStream;
import com.wizzardo.servlet.HttpRequest;
import com.wizzardo.servlet.ServletHttpConnection;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class ServletEpollInputStream extends EpollInputStream {

    protected AsyncServletInputStream asyncServletInputStream;

    public ServletEpollInputStream(ServletHttpConnection connection, byte[] buffer, int currentOffset, int currentLimit, long contentLength) {
        super(connection, buffer, currentOffset, currentLimit, contentLength);
        asyncServletInputStream = new AsyncServletInputStream(connection.getRequest());
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

    public ServletInputStream getServletInputStream() {
        return asyncServletInputStream;
    }

    class AsyncServletInputStream extends ServletInputStream {
        protected HttpRequest request;
        protected volatile ReadListenerWrapper listener;

        public AsyncServletInputStream(HttpRequest request) {
            this.request = request;
        }

        @Override
        public boolean isFinished() {
            return ServletEpollInputStream.this.isFinished();
        }

        @Override
        public boolean isReady() {
            int available = ServletEpollInputStream.this.available();
            if (available == 0) {
                try {
                    ServletEpollInputStream.this.fillBuffer();
                } catch (Exception e) {
                    if (listener != null)
                        listener.onError(e);
                    else
                        e.printStackTrace();
                }
                available = ServletEpollInputStream.this.available();
            }

            return available != 0;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            if (listener != null)
                throw new IllegalStateException("Listener was already set");
            listener = new ReadListenerWrapper(readListener);
        }

        @Override
        public int read() throws IOException {
            return ServletEpollInputStream.this.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return ServletEpollInputStream.this.read(b, off, len);
        }
    }
}
