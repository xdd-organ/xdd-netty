package com.java.xdd.netty.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
    //使用指定分隔符
    public void bind(int port) {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) //配置TCP参数
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-codec", new HttpServerCodec()); //
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536)); //
                            ch.pipeline().addLast("chunked", new ChunkedWriteHandler()); //
                            ch.pipeline().addLast("handler", new WebSocketServerHandler());
                        }
                    });

            //绑定端口,同步等待成功
            //ChannelFuture sync = b.bind(port).sync();
            Channel channel = b.bind(port).sync().channel();
            System.out.println("web socket server started at port " + port + ".");
            System.out.println("open you browser and navigate to http://localhost:" + port + "/");

            //等待服务端监听端口关闭
            //sync.channel().closeFuture().sync();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放NIO线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        new WebSocketServer().bind(port);
    }
}
