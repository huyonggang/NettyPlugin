package com.yunma.nettyplugin;

import android.app.Application;

/**
 * Created by huyg on 2017/12/20.
 */

public class App extends Application {

    private static App instance;
    public static synchronized App getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
