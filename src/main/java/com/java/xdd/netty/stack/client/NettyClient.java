package com.java.xdd.netty.stack.client;


import com.java.xdd.netty.stack.decoder.NettyMessageDecoder;
import com.java.xdd.netty.stack.encoder.NettyMessageEncoder;
import com.java.xdd.netty.stack.handler.HeartBeatRequestHandler;
import com.java.xdd.netty.stack.handler.LoginAuthRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    //线程池
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    //配置客户端NIO线程组
    private EventLoopGroup group = new NioEventLoopGroup();

    //使用指定分隔符
    public void connect(int port, String host) {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4)); //解码
                            ch.pipeline().addLast("messageEncoder", new NettyMessageEncoder()); //编码
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50)); //
                            ch.pipeline().addLast("loginAuthHandler", new LoginAuthRequestHandler()); //登录验证
                            ch.pipeline().addLast("heartBeatHandler", new HeartBeatRequestHandler()); //心跳
                        }
                    });

            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            InetSocketAddress socketAddress2 = new InetSocketAddress(host, port);
            //发起异步连接操作
            ChannelFuture sync = b.connect(host, port).sync();
            ChannelFuture sync2 = b.connect(socketAddress, socketAddress2).sync();

            //等待客户端链路关闭
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //断线重连
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        try {
                            connect(port, host);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        String host = "127.0.0.1";
        new NettyClient().connect(port, host);
    }
}
