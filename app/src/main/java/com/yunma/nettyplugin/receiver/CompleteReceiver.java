package com.yunma.nettyplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yunma.nettyplugin.service.ClientService;

/**
 * Created by huyg on 2018/11/11.
 */
public class CompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ClientService.class);
        context.startService(service);
    }
}
