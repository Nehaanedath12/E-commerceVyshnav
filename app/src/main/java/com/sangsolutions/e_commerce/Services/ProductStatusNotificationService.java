package com.sangsolutions.e_commerce.Services;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Home;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Receiver.BootCompletedReceiver;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;


public class ProductStatusNotificationService extends Service {


    public void showNotification(Context context, Class<?> cls, String title, String content,int notificationCode,String iId) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.putExtra("iOrderId",notificationCode);
        notificationIntent.putExtra("iId",iId);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificationCode, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "orderStatus");
        Notification notification = builder.setContentTitle(title)
                .setContentText(content)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationCode, notification);
        onDestroy();


    }

    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "orderStatus";
            String description = "for order status notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("orderStatus", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void LoadNotification(final Context context) {
        if (!Tools.getUserId(this).equals("0"))
            AndroidNetworking.get(URLs.getOrderConfirmAlert)
                    .addQueryParameter("iUser", Tools.getUserId(this))
                    .addQueryParameter("iStatus", "0")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.getString("getConfirmDetails"));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String iId = jsonObject.getString("iId");
                                    String iOrderId = jsonObject.getString("iOrderId");
                                    String sMessage = jsonObject.getString("sMessage");
                                    String sRefNo = jsonObject.getString("sRefNo");
                                    showNotification(context, Home.class, getString(R.string.app_name), sMessage,Integer.parseInt(iOrderId),iId);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("getOrderConfirmAlert\n"+e.getMessage()));
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, "onError: " + anError.getResponse());
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getOrderConfirmAlert\n"+anError.getErrorDetail()));
                        }
                    });
    }

    public void SetAlarm(Context context) {

        ComponentName receiver = new ComponentName(getApplicationContext(), BootCompletedReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), BootCompletedReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);


        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000, 10000, pendingIntent);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),60*1000, pendingIntent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this);
        }

        LoadNotification(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //SetAlarm(getApplicationContext());
    }
}