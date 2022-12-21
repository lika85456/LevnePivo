package com.lika85456.levnepivo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationsBroadcast", "onReceive");

        // reset alarm on boot or time change
        if (intent!=null && intent.getAction()!=null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.TIME_SET")) {
            // set alarm at 4pm every day
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 16);

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationsBroadcastReceiver.class), PendingIntent.FLAG_IMMUTABLE);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
            Log.d("NotificationsBroadcast", "Alarm set");
        }
        else{
            // TODO: notify user if there are new beer discounts
        }
    }


}
