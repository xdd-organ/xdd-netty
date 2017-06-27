package com.java.xdd.netty.stack.handler;

import com.java.xdd.netty.stack.pojo.Header;
import com.java.xdd.netty.stack.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 握手安全认证
 */
public class LoginAuthRequestHandler extends ChannelHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginRequest());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        //如果是握手应答消息，需判断是否认证成功
        if (message.getHeader() != null && message.getHeader().getType() == 1) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != 0) {
                //握手失败，关闭连接
                ctx.close();
            } else {
                System.out.println("lock is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    private NettyMessage buildLoginRequest() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType((byte) 1);
        message.setHeader(header);
        return message;
    }
}
