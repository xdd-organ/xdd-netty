package com.java.xdd.netty.stack.handler;

import com.java.xdd.netty.stack.pojo.Header;
import com.java.xdd.netty.stack.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳应答检测
 */
public class HeartBeatResponseHandler extends ChannelHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        //握手成功，返回心跳应答消息
        if (message.getHeader() != null) {
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("server receive heartBeat --> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType((byte) 1);
        message.setHeader(header);
        return message;
    }
}
