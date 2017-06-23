package com.java.xdd.netty.pojo.server;

import com.java.xdd.netty.pojo.pojo.SubscribeReq;
import com.java.xdd.netty.pojo.pojo.SubscribeResp;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if ("xdd".equalsIgnoreCase(req.getUsername())) {
            System.out.println("service accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(this.resp(req.getSubReqID()));
        }
    }

    private SubscribeResp resp(int i) {
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(i);
        resp.setRespCode(0);
        resp.setDesc("nice to meet you!");

        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
