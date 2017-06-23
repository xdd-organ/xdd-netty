package com.java.xdd.netty.udp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class ChineseProverServer {
    //使用指定分隔符
    public void bind(int port) {
        //配置服务端的NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class) //配置UDP通讯必要这个类
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChineseProverServerHandler());

            //绑定端口,同步等待成功
            b.bind(port).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        new ChineseProverServer().bind(port);
    }
}
