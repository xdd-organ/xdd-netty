package com.java.xdd.netty.stack.handler;

import com.java.xdd.netty.stack.pojo.Header;
import com.java.xdd.netty.stack.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 心跳请求检测机制
 */
public class HeartBeatRequestHandler extends ChannelHandlerAdapter{
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        //握手成功，主动发送心跳
        if (message.getHeader() != null) {
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatRequestHandler.HeartBeatTask(ctx),
                    0, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader() != null) {
            System.out.println("client receive server heart --> " + message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        System.out.println(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage message = buildHeartBeat();
            System.out.println("client send heartBeat --> " + message);
            ctx.writeAndFlush(message);
        }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType((byte) 1);
            message.setHeader(header);
            return message;
        }
    }
}
