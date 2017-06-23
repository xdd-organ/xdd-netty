package com.java.xdd.netty.stack.handler;

import com.java.xdd.netty.stack.pojo.Header;
import com.java.xdd.netty.stack.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//登录验证
public class LoginAuthResponseHandler extends ChannelHandlerAdapter{
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    private String[] whiteList = {"127.0.0.1", "192.168.1.22"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        //如果是握手请求消息，处理，其他消息透传
        if (message.getHeader() != null && message.getHeader().getType() == 1) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            //重复登录拒绝
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = this.buildResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;

                for (String s : whiteList) {
                    if (s.equals(ip)) {
                        isOk = true;
                        break;
                    }
                }

                loginResp = isOk ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if (isOk) {
                    nodeCheck.put(nodeIndex, true);
                }
            }
            System.out.println(loginResp);
            ctx.writeAndFlush(loginResp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte b) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType((byte) 1);
        message.setHeader(new Header());
        message.setBody(b);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString()); //删除缓存
        cause.printStackTrace();
        System.out.println(cause.getMessage());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
