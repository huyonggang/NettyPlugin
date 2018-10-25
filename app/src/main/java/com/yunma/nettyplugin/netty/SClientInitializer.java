package com.yunma.nettyplugin.netty;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by dayaa on 15/8/11.
 */
public class SClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ch.pipeline().addLast(new StringEncoder());
        ch.pipeline().addLast(new StringDecoder());
        pipeline.addLast("ping", new IdleStateHandler(60, 60*3, 60 * 10, TimeUnit.SECONDS));
        pipeline.addLast(new DecoderHandler());
    }
}
