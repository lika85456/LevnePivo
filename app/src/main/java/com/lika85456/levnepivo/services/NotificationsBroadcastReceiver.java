package com.lika85456.levnepivo.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.lika85456.levnepivo.MainActivity;
import com.lika85456.levnepivo.R;
import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.BeerDiscountsStorage;
import com.lika85456.levnepivo.lib.LovedBeersStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Notifies user when new discounts are available. Starts every day at 15 (+-)
 */
public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "levnepivo_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationsBroadcast", "onReceive");

        try {
            // reset alarm on boot or time change
            if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.TIME_SET")) {
                resetAlarm(context);
            } else {
                notify(context);
            }
        } catch (Exception e) {
            Log.e("NotificationsBroadcast", "Error", e);
        }

    }

    private boolean notify(Context context) throws IOException {
        // get discounts
        BeerDiscountsStorage beerDiscountsStorage = new BeerDiscountsStorage(context);
        LovedBeersStorage lovedBeersStorage = new LovedBeersStorage(context);
        ArrayList<BeerAPI.BeerDiscount> result = BeerAPI.fetchDiscounts();

        String text = getNewDiscountsNotificationText(beerDiscountsStorage, result, lovedBeersStorage.getLovedBeers());

        // save new discounts
        for (int i = 0; i < result.size(); i++) {
            if (!result.contains(result.get(i).beer.name)) {
                result.remove(i);
                i--;
            }
        }

        if (text == null) {
            return true;
        }

        // save new discounts
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

        notificationManager.notify((int) System.currentTimeMillis(), notification);

        Log.d("NotificationsBroadcast", "Notification sent");

        return false;
    }

    private void resetAlarm(Context context) {
        // set alarm at 4pm every day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent alarmIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationsBroadcastReceiver.class), PendingIntent.FLAG_IMMUTABLE);
        } else {
            throw new RuntimeException("Not supported android version");
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        Log.d("NotificationsBroadcast", "Alarm set");
    }

    /**
     * Returns text for notification when new beers are loaded
     *
     * @param beerDiscountsStorage
     * @param newDiscounts
     * @param lovedBeers           names of loved beers
     * @returns null if no new discounts are available
     */
    private String getNewDiscountsNotificationText(BeerDiscountsStorage beerDiscountsStorage, ArrayList<BeerAPI.BeerDiscount> newDiscounts, ArrayList<String> lovedBeers) {
        String notificationText = "";

        for (String lovedBeer : lovedBeers) {
            Float oldPrice = beerDiscountsStorage.getBeerDiscount(lovedBeer);

            Float newPrice = null;
            BeerAPI.BeerDiscount newDiscount = getBeerDiscountByBeerName(newDiscounts, lovedBeer);
            if (newDiscount != null) {
                newPrice = newDiscount.discounts.get(0).pricePerVolume.price;
            }

            if (newPrice != null && (!oldPrice.equals(newPrice))) {
                notificationText += lovedBeer + " - " + newPrice + ",- Kč\n";
            }
        }

        return notificationText;
    }

    private BeerAPI.BeerDiscount getBeerDiscountByBeerName(ArrayList<BeerAPI.BeerDiscount> beerDiscounts, String beerName) {
        for (BeerAPI.BeerDiscount beerDiscount : beerDiscounts) {
            if (beerDiscount.beer.name.equals(beerName)) {
                return beerDiscount;
            }
        }
        return null;
    }

}
