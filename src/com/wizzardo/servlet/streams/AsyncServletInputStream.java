package com.wizzardo.servlet.streams;

import com.wizzardo.servlet.HttpRequest;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class AsyncServletInputStream extends ServletInputStream {
    protected HttpRequest request;
    protected ServletEpollInputStream stream;
    protected volatile ReadListenerWrapper listener;

    public AsyncServletInputStream(HttpRequest request, ServletEpollInputStream stream) {
        this.request = request;
        this.stream = stream;
    }

    @Override
    public boolean isFinished() {
        return stream.isFinished();
    }

    @Override
    public boolean isReady() {
        int available = stream.available();
        if (available == 0) {
            try {
                stream.fillBuffer();
            } catch (Exception e) {
                if (listener != null)
                    listener.onError(e);
                else
                    e.printStackTrace();
            }
            available = stream.available();
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
        return stream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return stream.read(b, off, len);
    }

}
