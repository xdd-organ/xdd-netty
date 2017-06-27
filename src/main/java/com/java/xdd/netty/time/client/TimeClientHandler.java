package com.java.xdd.netty.time.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutor;
import org.junit.Test;

import java.net.SocketAddress;

public class TimeClientHandler extends ChannelHandlerAdapter{
    private int counter;
    private byte[] req;

    public TimeClientHandler() {
        req = ("query time order" + System.getProperty("line.separator")).getBytes();
    }

    /**
     * 当Channel变成活跃状态时被调用；Channel是连接/绑定、就绪的
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
        Channel channel = ctx.channel();
        String name = ctx.name();
        ByteBufAllocator alloc = ctx.alloc();
        EventExecutor executor = ctx.executor();
    }

    /**
     * Channel，如果数据被读取成功，触发该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("now is :" + body + " ; the counter is :" + ++counter);
    }

    /**
     * 读取操作API完成之后，紧接着调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("读取操作完成！");
    }

    /**
     * 异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }

    /**
     * 通道关闭
     *      ChannelFuture.close()调用时(主动关闭连接)，会主动触发该方法
     * @param ctx
     * @param promise
     * @throws Exception
     */
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("通道关闭");
    }

    /**
     * 连接销毁
     *      ChannelFuture.disconnect()调用时(请求断开与远程通信端的连接)，会主动触发该方法
     * @param ctx
     * @param promise
     * @throws Exception
     */
    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("断开与远程通信端的连接");
    }

    /**
     * 客户端主动连接服务器
     *      ChannelFuture.connect()调用时(客户端使用指定的服务端地址remoteAddress发起连接)，会主动触发该方法
     * @param ctx
     * @param remoteAddress
     * @param localAddress
     * @param promise
     * @throws Exception
     */
    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("客户端主动连接服务器");
    }

    /**
     * 绑定指定本地地址
     *      ChannelFuture.bind() 调用时(绑定指定的本地Socket地址localAddress)，会主动触发该方法
     * @param ctx
     * @param localAddress
     * @param promise
     * @throws Exception
     */
    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("绑定指定地址！");
        Channel channel = ctx.channel();
        EventLoop eventLoop = channel.eventLoop();
        Channel parent = channel.parent();
        ChannelId id = channel.id();
    }

    @Test
    public void test() {
        try {
            assert false : "输入有误";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
