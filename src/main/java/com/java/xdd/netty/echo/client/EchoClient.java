package com.java.xdd.netty.echo.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.io.ObjectInput;
import java.io.ObjectInputStream;

public class EchoClient {
    //使用指定分隔符
    public void connet(int port, String host) {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes()); //使用$_做分隔符

                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter)); //1024最大消息长度
                            ch.pipeline().addLast(new StringDecoder()); //接收到的消息转换成字符串
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            //发起异步连接操作
            ChannelFuture sync = b.connect(host, port).sync();

            //等待客户端链路关闭
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        String host = "127.0.0.1";
        new EchoClient().connet(port, host);
    }
}
