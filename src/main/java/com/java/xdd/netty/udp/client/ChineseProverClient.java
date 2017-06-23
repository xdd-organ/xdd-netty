package com.java.xdd.netty.udp.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class ChineseProverClient {
    //使用指定分隔符
    public void connet(int port, String host) {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChineseProverClientHandler());

            Channel channel = b.bind(0).sync().channel();
            //向网段内的所有机器广播UDP消息
            channel.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("谚语字典查询？", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255", port))).sync();

            //等待客户端链路关闭
            if (!channel.closeFuture().await(15000)) {
                System.out.println("查询超时！");
            }

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
        new ChineseProverClient().connet(port, host);
    }
}
