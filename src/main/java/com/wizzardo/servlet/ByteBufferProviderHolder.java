package com.wizzardo.servlet;

import com.wizzardo.epoll.ByteBufferProvider;
import com.wizzardo.epoll.ByteBufferWrapper;

/**
 * Created by wizzardo on 21.03.15.
 */
public class ByteBufferProviderHolder {

    private static ThreadLocal<ByteBufferProvider> bufferProvider = new ThreadLocal<ByteBufferProvider>() {
        @Override
        protected ByteBufferProvider initialValue() {
            return new ByteBufferProvider() {
                protected ByteBufferWrapper byteBufferWrapper = new ByteBufferWrapper(1024 * 50);

                @Override
                public ByteBufferWrapper getBuffer() {
                    return byteBufferWrapper;
                }
            };
        }
    };


    public static ByteBufferProvider get() {
        return bufferProvider.get();
    }
}
