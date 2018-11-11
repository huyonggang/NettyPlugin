package com.yunma.nettyplugin.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ScrollView;

import com.yunma.nettyplugin.global.Const;
import com.yunma.nettyplugin.netty.SClientManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huyg on 2018/10/18.
 */
public class ClientService extends Service {

    private boolean isAppRunning = false;
    private long sendTime = 0;

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
        initDispose();
    }

    private void initDispose() {
        Disposable disposable = Observable.interval(0, 10 * 60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!isBackground()) {
                            startApp();
                            return;
                        }
                        if (System.currentTimeMillis() - sendTime > 1000 * 60 * 10) {
                            startApp();
                        }
                    }
                });
    }


    @Subscribe
    public void onEvent(String data) {
        sendTime = System.currentTimeMillis();
        SClientManager.getInstance().sendFrame(data + "\n");
    }


    public boolean isBackground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals("com.tianheng.client") && info.baseActivity.getPackageName().equals("com.tianheng.client")) {

                isAppRunning = true;

                break;
            } else {
                isAppRunning = false;
            }
        }

        Log.d("ClientService", "status--->" + isAppRunning);
        return isAppRunning;

    }


    private void startApp() {
        Intent launchIntent = this.getPackageManager().
                getLaunchIntentForPackage("com.tianheng.client");
        launchIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(launchIntent);
    }

}
