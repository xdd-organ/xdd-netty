package com.java.xdd.netty.stack.server;

import com.java.xdd.netty.stack.decoder.NettyMessageDecoder;
import com.java.xdd.netty.stack.encoder.NettyMessageEncoder;
import com.java.xdd.netty.stack.handler.HeartBeatResponseHandler;
import com.java.xdd.netty.stack.handler.LoginAuthResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.junit.Test;

public class NettyServer {
    //使用指定分隔符
    public void bind(int port) {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) //配置TCP参数
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4)); //解码
                            ch.pipeline().addLast("messageEncoder", new NettyMessageEncoder()); //编码
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50)); //
                            ch.pipeline().addLast("loginAuthHandler", new LoginAuthResponseHandler()); //登录验证
                            ch.pipeline().addLast("heartBeatHandler", new HeartBeatResponseHandler()); //心跳
                        }
                    });

            //绑定端口,同步等待成功
            ChannelFuture sync = b.bind(port).sync();

            //等待服务端监听端口关闭
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放NIO线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        new NettyServer().bind(port);
    }

    @Test
    public void test2() {
        String a = "123456";
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        byte[] encode = encoder.encode(a.getBytes());
        String s = new String(encode);
        System.out.println(s); //MTIzNDU2

        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        byte[] decode = decoder.decode(s.getBytes());
        String s1 = new String(decode);
        System.out.println(s1);
    }



}
