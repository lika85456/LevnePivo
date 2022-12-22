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

        // assert empty
        assertEquals(0, storage.getBeerDiscounts().size());

        ArrayList<BeerAPI.BeerDiscount> beers = new ArrayList<>();
        BeerAPI.BeerDiscount b1 = new BeerAPI.BeerDiscount();
        b1.beer.name = "beer1";
        b1.beer.imageUrl = "https://beer1";
        b1.discounts = new ArrayList<>();
        beers.add(b1);

        BeerAPI.Beer beer = b1.beer;

        storage.setBeerDiscounts(beers);

        storage = new BeerDiscountsStorage(appContext);
        assertEquals(1,storage.getBeerDiscounts().size());
        assertEquals("beer1",storage.getBeerDiscounts().get(0).beer.name);

        storage.setBeerDiscounts(new ArrayList<>());
    }


}