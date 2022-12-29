package com.lika85456.levnepivo.lib;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Android storage for loved beer discounts.
 * This is used to later notify user when new discounts are available.
 */
public class BeerDiscountsStorage {
    private static final String STORAGE_NAME = "beer_discounts_storage";
    private final KeyValueStorage storage;

    /**
     * Prepares and loads storage
     */
    public BeerDiscountsStorage(Context context) {
        storage = new KeyValueStorage(context, STORAGE_NAME);
    }

    public Float getBeerDiscount(String beerName) {
        return Float.valueOf(storage.get(beerName, ""));
    }

    public void setBeerDiscounts(ArrayList<BeerAPI.BeerDiscount> beerDiscounts) {
        for (BeerAPI.BeerDiscount beerDiscount : beerDiscounts) {
            Log.d("BeerDiscountsStorage", "setBeerDiscounts: " + beerDiscount.beer.name + " " + beerDiscount.discounts.get(0).pricePerVolume.price);
            storage.set(beerDiscount.beer.name, String.valueOf(beerDiscount.discounts.get(0).pricePerVolume.price));
        }
    }

    public void clear() {
        storage.clear();
    }
}
