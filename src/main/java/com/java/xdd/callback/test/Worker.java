package com.java.xdd.callback.test;

public class Worker {

    public void doWoke() {
        Fetcher fetcher = new MyFetcher(new Data(1, 2));
        fetcher.fetchData(new FetcherCallback() {
            @Override
            public void onData(Data data) throws Exception {
                System.out.println(data);
            }

            @Override
            public void onError(Throwable cause) {
                cause.printStackTrace();
            }
        });
    }


    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.doWoke();
    }
}
