package com.yunma.nettyplugin.netty;

import android.content.Context;
import android.util.Log;

import com.yunma.nettyplugin.global.Const;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by huyg on 18/1/10.
 */
public class SClientManager {
    private final String TAG = getClass().getSimpleName();
    public static volatile SClientManager instance = null;

    private Context context;
    public boolean isRunning = false;
    public boolean flag = false;
    public boolean isNetty = true;

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private ChannelFuture lastWriteFuture;
    private boolean isConnect = false;

    public static SClientManager getInstance() {
        if (instance == null) {
            synchronized (SClientManager.class) {
                if (instance == null) {
                    instance = new SClientManager();
                }
            }
        }
        return instance;
    }


    public void start(final String ip, final String port) {
        Log.i(TAG, "SClientManager is starting...");
        startNetty(ip, port);
    }

    public void stop() {
        Log.i(TAG, "SClientManager is stopping...");
        stopNetty();
    }

    private void startNetty(String ip, String port) {
        Log.i(TAG, "netty is starting...");
        if (group == null)
            group = new NioEventLoopGroup();
        if (bootstrap == null)
            bootstrap = new Bootstrap();
        if (!flag) {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new SClientInitializer())
            ;
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_TIMEOUT, 5000);
            flag = true;
        }
        try {
            channel = bootstrap.connect(ip, Integer.parseInt(port)).addListener(new ConnectionListener(this)).sync().channel();
            isRunning = true;
            Log.e(TAG, "netty connect success");
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Log.i(TAG, "netty start failed...InterruptedException");
            } else if (e instanceof ConnectTimeoutException) {
                Log.e(TAG, "netty start failed, connection time out");
            } else if (e instanceof ConnectException) {
                Log.e(TAG, "netty start failed, No route to host");
            }
            e.printStackTrace();
        }
    }

    private void stopNetty() {
        try {
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                Log.i(TAG, "lastWriteFuture is sync...");
                lastWriteFuture.sync();
            }
            if (channel != null) {
                Log.i(TAG, "channel closeFuture...");
                channel.close();
            }
            if (group != null) {
                Log.i(TAG, "MGroup shutdownGracefully...");
                group.shutdownGracefully();
            }
            isRunning = false;
        } catch (InterruptedException e) {
            Log.i(TAG, "netty stop failed...");
            e.printStackTrace();
        }
    }


    public boolean sendFrame(String frame) {
        if (frame == null) {
            Log.e(TAG, "frame sent failed, it can not be null");
            return false;
        }
        if (channel == null) {
            Log.e(TAG, "frame sent failed, channel is null, is the manager start?");
            return false;
        }
        channel.writeAndFlush(frame);
        Log.d(TAG, frame);
        return true;
    }


    public class ConnectionListener implements ChannelFutureListener {


        private SClientManager client;


        public ConnectionListener(SClientManager client) {
            this.client = client;
        }


        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                System.out.println("Reconnection");
                final EventLoop eventLoop = future.channel().eventLoop();
                eventLoop.schedule(new Runnable() {

                    @Override
                    public void run() {
                        start(Const.BASE_IP, Const.BASE_PORT);
                    }
                }, 1, TimeUnit.SECONDS);
            }
        }


    }


}