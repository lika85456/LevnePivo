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

import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.BeerDiscountsStorage;
import com.lika85456.levnepivo.lib.LovedBeersStorage;
import com.lika85456.levnepivo.services.NewBeerDiscountsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "levnepivo_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationsBroadcast", "onReceive");

        // reset alarm on boot or time change
        if (intent!=null && intent.getAction()!=null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.TIME_SET")) {
            // set alarm at 4pm every day
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 15);

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationsBroadcastReceiver.class), PendingIntent.FLAG_IMMUTABLE);

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
            Log.d("NotificationsBroadcast", "Alarm set");
        }
        else{
            // get discounts
            BeerDiscountsStorage beerDiscountsStorage = new BeerDiscountsStorage(context);
            LovedBeersStorage lovedBeersStorage = new LovedBeersStorage(context);
            try {
                ArrayList<BeerAPI.BeerDiscount> result = BeerAPI.fetchDiscounts();

                String text = NewBeerDiscountsService.getNewDiscountsNotificationText(beerDiscountsStorage.getBeerDiscounts(), result, lovedBeersStorage.getLovedBeers());

                // save new discounts
                for (int i = 0; i < result.size(); i++) {
                    if (!result.contains(result.get(i).beer.name)) {
                        result.remove(i);
                        i--;
                    }
                }

                beerDiscountsStorage.setBeerDiscounts(result);

                // send notification
                Intent mainActivityIntent = new Intent(context, MainActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Nové pivní slevy jsou tady!")
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(mainActivityPendingIntent)
                        .build();

                notificationManager.notify((int)System.currentTimeMillis(), notification);

                Log.d("NotificationsBroadcast", "Notification sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
