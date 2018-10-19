package com.yunma.nettyplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yunma.nettyplugin.service.ClientService;

/**
 * Created by huyg on 2018/10/18.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ClientService.class);
        context.startService(service);
    }
}
