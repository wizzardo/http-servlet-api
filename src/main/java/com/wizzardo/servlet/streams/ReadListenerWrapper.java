package com.wizzardo.servlet.streams;

import javax.servlet.ReadListener;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class ReadListenerWrapper implements ReadListener {
    private ReadListener listener;

    public ReadListenerWrapper(ReadListener listener) {
        if (listener == null)
            throw new NullPointerException();

        this.listener = listener;
    }

    @Override
    public void onDataAvailable() {
        try {
            listener.onDataAvailable();
        } catch (Throwable e) {
            onError(e);
        }
    }

    @Override
    public void onAllDataRead() {
        try {
            listener.onAllDataRead();
        } catch (Throwable e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable t) {
        listener.onError(t);
    }
}
