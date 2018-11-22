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
    public static final String ACTION_TIME_SET = "android.intent.action.TIME_SET";
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
                0, mFifteenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        /**如果超过今天的3点，那么定时器就设置为明天3点*/
//        if (systemTime > selectTime) {
//            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
//        }
        if (systemTime > selectTime) {
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 10);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String selectStr = sdf.format(new Date(calendar.getTimeInMillis()));
        long clockTime = SystemClock.elapsedRealtime();
        /**RTC_SHUTDOWN_WAKEUP 使用标识，系统进入深度休眠还唤醒*/
        mg.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, clockTime + calendar.getTimeInMillis() - systemTime, AlarmManager.INTERVAL_DAY, p);
    }

    public static void reboot() {
        try {
            Log.i(TAG, "reboot");
            Runtime.getRuntime().exec("am start -a android.intent.action.REBOOT");
        } catch (Exception e) {
        }
    }
}
