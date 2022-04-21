package com.sangsolutions.e_commerce.Receiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.sangsolutions.e_commerce.Services.ProductStatusNotificationService;

import java.util.Calendar;


public class BootCompletedReceiver extends BroadcastReceiver {


    public void SetAlarm(Context context) {

        ComponentName receiver = new ComponentName(context, BootCompletedReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


       /* AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, ProductStatusNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000, 10000, pendingIntent);*/

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),60*1000, pendingIntent);

        Intent intent = new Intent(context, ProductStatusNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 60*1000, pendingIntent);
    }


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (context != null) {
                SetAlarm(context);
            context.startService(new Intent(context, ProductStatusNotificationService.class));


        }
    }
}
