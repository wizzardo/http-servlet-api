package com.wizzardo.servlet.streams;

import javax.servlet.WriteListener;

/**
 * @author: wizzardo
 * Date: 05.11.14
 */
public class WriteListenerWrapper implements WriteListener {
    private WriteListener listener;

    public WriteListenerWrapper(WriteListener listener) {
        if (listener == null)
            throw new NullPointerException();

        this.listener = listener;
    }

    @Override
    public void onWritePossible() {
        try {
            listener.onWritePossible();
        } catch (Throwable e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable t) {
        listener.onError(t);
    }
}
