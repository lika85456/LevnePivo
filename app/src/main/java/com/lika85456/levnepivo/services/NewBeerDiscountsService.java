package com.lika85456.levnepivo.services;

import android.util.Log;

import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.BeerDiscountsStorage;

import java.util.ArrayList;

public class NewBeerDiscountsService {
    /**
     * Returns text for notification when new beers are loaded
     * @param beerDiscountsStorage
     * @param newDiscounts
     * @param lovedBeers names of loved beers
     * @returns null if no new discounts are available
     */
    public static String getNewDiscountsNotificationText(BeerDiscountsStorage beerDiscountsStorage, ArrayList<BeerAPI.BeerDiscount> newDiscounts, ArrayList<String> lovedBeers) {
        String notificationText = "";

        for (String lovedBeer : lovedBeers) {
            Float oldPrice = beerDiscountsStorage.getBeerDiscount(lovedBeer);

            Float newPrice = null;
            BeerAPI.BeerDiscount newDiscount = getBeerDiscountByBeerName(newDiscounts, lovedBeer);
            if (newDiscount != null) {
                newPrice = newDiscount.discounts.get(0).pricePerVolume.price;
            }

            if(newPrice != null && (!oldPrice.equals(newPrice))) {
                notificationText += lovedBeer + " - " + newPrice + ",- Kč\n";
            }
        }

        return notificationText;
    }

    private static BeerAPI.BeerDiscount getBeerDiscountByBeerName(ArrayList<BeerAPI.BeerDiscount> beerDiscounts, String beerName){
        for(BeerAPI.BeerDiscount beerDiscount : beerDiscounts){
            if(beerDiscount.beer.name.equals(beerName)){
                return beerDiscount;
            }
        }
        return null;
    }

}
