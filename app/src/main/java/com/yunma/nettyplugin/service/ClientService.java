package com.yunma.nettyplugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ScrollView;

import com.yunma.nettyplugin.global.Const;
import com.yunma.nettyplugin.netty.SClientManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by huyg on 2018/10/18.
 */
public class ClientService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        SClientManager.getInstance().start(Const.BASE_IP, Const.BASE_PORT);
    }


    @Subscribe
    public void onEvent(String data) {
        SClientManager.getInstance().sendFrame(data);
    }

}
