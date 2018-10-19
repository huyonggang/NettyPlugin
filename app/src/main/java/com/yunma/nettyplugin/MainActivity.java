package com.yunma.nettyplugin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.yunma.nettyplugin.bean.PingBean;
import com.yunma.nettyplugin.util.Util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Intent intent = new Intent();
    private String pingStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
//        Intent intent = new Intent();
//        intent.setAction("com.yunma.start");
//        sendBroadcast(intent);
        //initDispose();
    }

    private void initData() {
        PingBean pingBean = new PingBean();
        pingBean.setType(0);
        pingBean.setCabinetNumber(Util.getImei(App.getInstance()));
        pingStr = new Gson().toJson(pingBean);
    }

    private void initDispose() {
        intent.setAction("com.yunma.netty");
        Disposable disposable = Observable.interval(20, 4, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        intent.putExtra("data", pingStr);
                        sendBroadcast(intent);
                    }
                });
    }


}
