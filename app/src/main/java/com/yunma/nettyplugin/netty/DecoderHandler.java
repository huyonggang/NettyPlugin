package com.yunma.nettyplugin.netty;


import com.google.gson.Gson;
import com.yunma.nettyplugin.App;
import com.yunma.nettyplugin.bean.PingBean;
import com.yunma.nettyplugin.global.Const;
import com.yunma.nettyplugin.util.Util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by huyg on 2018/1/19.
 */

public class DecoderHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("服务器数据"+o);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("长期没收到服务器推送数据");
                //可以选择重新连接
                // SClientManager.getInstance().stop();
                //SClientManager.getInstance().start(Const.BASE_IP, Const.BASE_PORT);

            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("长期未向服务器发送数据");
                //发送心跳包
                PingBean pingBean = new PingBean();
                pingBean.setType(0);
                pingBean.setCabinetNumber(Util.getImei(App.getInstance()));
                String pingStr = new Gson().toJson(pingBean);
                ctx.writeAndFlush(pingStr);
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL");
            }

        }
    }
}
