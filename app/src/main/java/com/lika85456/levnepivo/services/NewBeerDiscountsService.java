package com.lika85456.levnepivo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.lika85456.levnepivo.MainActivity;
import com.lika85456.levnepivo.R;

/**
 * This class is responsible for checking new beer discounts and showing notifications if there are any.
 */
public class NewBeerDiscountsService {

    public onAlarm(Context context) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE);


        // send notifications
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // send notification on channel "channel_id"
        Notification notification = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle("Seznam slev na pivo!")
                .setContentText("Zkontrolujte si seznam slev na pivo!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(mainActivityPendingIntent)
                .build();

        notificationManager.notify((int)System.currentTimeMillis(), notification);

    }
}
