package com.java.xdd.netty.udp.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class ChineseProverClientHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String content = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(content);

        if (content.startsWith("谚语查询结果")) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
