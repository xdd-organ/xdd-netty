package com.java.xdd.callback.test;

public interface FetcherCallback {
    void onData(Data data) throws Exception;
    void onError(Throwable cause);
}
