package com.lika85456.levnepivo.services;

import android.util.Log;

import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.BeerDiscountsStorage;

import java.util.ArrayList;

public class NewBeerDiscountsService {
    /**
     * Will notify if new loved beer discounts are available
     * @param oldDiscounts
     * @param newDiscounts
     * @param lovedBeers names of loved beers
     */
    public static String getNewDiscountsNotificationText(ArrayList<BeerAPI.BeerDiscount> oldDiscounts, ArrayList<BeerAPI.BeerDiscount> newDiscounts, ArrayList<String> lovedBeers) {
        // remove non loved beers from new discounts
        for (int i = 0; i < newDiscounts.size(); i++) {
            if (!lovedBeers.contains(newDiscounts.get(i).beer.name)) {
                newDiscounts.remove(i);
                i--;
            }
        }

        // remove non loved beers from old discounts
        for (int i = 0; i < oldDiscounts.size(); i++) {
            if (!lovedBeers.contains(oldDiscounts.get(i).beer.name)) {
                oldDiscounts.remove(i);
                i--;
            }
        }

        String notificationText = "";

        for (String beer : lovedBeers) {
            // get new discount
            BeerAPI.BeerDiscount newDiscount = null;
            for (BeerAPI.BeerDiscount discount : newDiscounts) {
                if (discount.beer.name.equals(beer)) {
                    newDiscount = discount;
                    break;
                }
            }

            // get old discount
            BeerAPI.BeerDiscount oldDiscount = null;
            for (BeerAPI.BeerDiscount discount : oldDiscounts) {
                if (discount.beer.name.equals(beer)) {
                    oldDiscount = discount;
                    break;
                }
            }

            if (newDiscount != null && (oldDiscount == null || newDiscount.discounts.get(0).pricePerVolume.price != oldDiscount.discounts.get(0).pricePerVolume.price)) {
                notificationText += beer + " - " + newDiscount.discounts.get(0).pricePerVolume.price + ",- Kč\n";
            }
        }

        if (notificationText.equals("")) {
            return null;
        }

        return notificationText;

    }

}
