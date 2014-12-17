package com.wizzardo.servlet.streams;

import com.wizzardo.servlet.HttpRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class AsyncServletOutputStream extends ServletOutputStream {
    protected HttpRequest request;
    protected ServletEpollOutputStream stream;
    protected volatile WriteListenerWrapper listener;

    public AsyncServletOutputStream(HttpRequest request, ServletEpollOutputStream stream) {
        this.request = request;
        this.stream = stream;
    }

    @Override
    public boolean isReady() {
        return stream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        if (listener != null)
            throw new IllegalStateException("Listener was already set");
        listener = new WriteListenerWrapper(writeListener);
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }
}
