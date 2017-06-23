package com.java.xdd.netty.udp.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ThreadLocalRandom;

public class ChineseProverServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    private static final String[] dictionary = {"只要功夫深，铁柱磨成针", "旧时王谢堂前燕，飞入寻常百姓家"
            , "洛阳亲友如相问，一片冰心在玉壶", "一寸光阴一寸金，寸金难买寸光阴", "老骥伏枥，志在千里"};

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String content = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(content);

        if ("谚语字典查询？".equals(content)) {
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("谚语查询结果：" + nextQuote(), CharsetUtil.UTF_8),
                    packet.sender()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println(cause.getMessage());
        ctx.close();
    }

    private String nextQuote() {
        int quoteId = ThreadLocalRandom.current().nextInt(dictionary.length);
        return dictionary[quoteId];
    }
}
