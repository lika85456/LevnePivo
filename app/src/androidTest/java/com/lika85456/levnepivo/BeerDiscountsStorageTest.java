package com.lika85456.levnepivo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.lika85456.levnepivo.lib.BeerAPI;
import com.lika85456.levnepivo.lib.BeerDiscountsStorage;
import com.lika85456.levnepivo.lib.LovedBeersStorage;

import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BeerDiscountsStorageTest {
    @Test
    public void itSavesAndLoadsProperly() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BeerDiscountsStorage storage = new BeerDiscountsStorage(appContext);
        storage.clear();

        ArrayList<BeerAPI.BeerDiscount> beers = new ArrayList<>();
        BeerAPI.BeerDiscount b1 = new BeerAPI.BeerDiscount();
        b1.beer.name = "beer1";
        b1.beer.imageUrl = "https://beer1";
        b1.discounts = new ArrayList<>();

        BeerAPI.BeerProviderDiscount d1 = new BeerAPI.BeerProviderDiscount();
        d1.pricePerVolume = new BeerAPI.PricePerVolume("10,0 Kč / 0.5 l");

        b1.discounts.add(d1);

        beers.add(b1);

        storage.setBeerDiscounts(beers);

        storage = new BeerDiscountsStorage(appContext);

        assertEquals(10, storage.getBeerDiscount("beer1"), 0.001);

        storage.clear();
    }


}