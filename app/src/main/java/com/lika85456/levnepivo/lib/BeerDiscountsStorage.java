package com.lika85456.levnepivo.lib;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class BeerDiscountsStorage {
    private KeyValueStorage storage;
    private ArrayList<BeerAPI.BeerDiscount> beerDiscounts;

    private static String STORAGE_NAME = "beer_discounts_storage";
    private static String KEY = "beer_discounts";

    public BeerDiscountsStorage(Context context){
        storage = new KeyValueStorage(context, STORAGE_NAME);
        beerDiscounts = new ArrayList<>();

        String data = storage.get(KEY, "[]");

        // parse from json array
        BeerAPI.BeerDiscount[] discounts;

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);
            discounts = new BeerAPI.BeerDiscount[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                discounts[i] = BeerAPI.BeerDiscount.fromString(String.valueOf(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e("BeerDiscountsStorage", "Error while parsing json array", e);
        }
    }

    public ArrayList<BeerAPI.BeerDiscount> getBeerDiscounts(){
        return beerDiscounts;
    }

    public void setBeerDiscounts(ArrayList<BeerAPI.BeerDiscount> beerDiscounts){
        this.beerDiscounts = beerDiscounts;
        save();
    }

    private void save(){
        JSONArray jsonArray = new JSONArray();
        for (BeerAPI.BeerDiscount beerDiscount : beerDiscounts) {
            jsonArray.put(beerDiscount.toJSON());
        }
        storage.set(KEY, jsonArray.toString());
    }
}
