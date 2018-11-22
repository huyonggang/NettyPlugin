package com.yunma.nettyplugin.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.yunma.nettyplugin.service.ClientService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huyg on 2018/11/11.
 */


public class CompleteReceiver extends BroadcastReceiver {


    public static final String ACTION_THREE_CLOCK_REBOOT = "ACTION_THREE_CLOCK_REBOOT";
    public static final String ACTION_TIME_SET = "android.intent.action.TIME_TICK";
    public static final String ACTION_BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";
    public static final String TAG = "CompleteReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_TIME_SET:
                start3Clock(context);
                break;
            case ACTION_THREE_CLOCK_REBOOT:
                reboot();
                break;
            case ACTION_BOOT_COMPLETE:
                Intent service = new Intent(context, ClientService.class);
                context.startService(service);
                break;
        }

    }

    public void start3Clock(Context context) {
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mFifteenIntent = new Intent(ACTION_THREE_CLOCK_REBOOT);
        PendingIntent p = PendingIntent.getBroadcast(context,
                0, mFifteenIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 40);
        long selectTime = calendar.getTimeInMillis();
        mg.setRepeating(AlarmManager.RTC, selectTime, AlarmManager.INTERVAL_DAY, p);
    }

    public static void reboot() {
        try {
            Log.i(TAG, "reboot");
            Runtime.getRuntime().exec("am start -a android.intent.action.REBOOT");
        } catch (Exception e) {
        }
    }
}
