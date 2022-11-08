package com.lika85456.levnepivo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.lika85456.levnepivo.lib.LovedBeersStorage;

import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LovedBeersStorageTest {
    @Test
    public void itSavesAndLoadsProperly() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LovedBeersStorage storage = new LovedBeersStorage(appContext);
        storage.add("beer1");

        storage = new LovedBeersStorage(appContext);
        assertEquals(storage.getLovedBeers().size(), 1);
        assertEquals(storage.getLovedBeers().get(0), "beer1");

        storage.remove("beer1");
        assertEquals(storage.getLovedBeers().size(), 0);

        storage = new LovedBeersStorage(appContext);
        assertEquals(storage.getLovedBeers().size(), 0);
    }

    @Test
    public void itDoesNotAddSameBeerTwice() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LovedBeersStorage storage = new LovedBeersStorage(appContext);
        storage.add("beer1");
        storage.add("beer1");

        storage = new LovedBeersStorage(appContext);
        assertEquals(storage.getLovedBeers().size(), 1);
        assertEquals(storage.getLovedBeers().get(0), "beer1");

        storage.remove("beer1");
    }

    @Test
    public void itCallsOnChangeListener(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LovedBeersStorage storage = new LovedBeersStorage(appContext);

        LovedBeersStorage.OnChangeListener listener = new LovedBeersStorage.OnChangeListener() {
            @Override
            public void onChange(ArrayList<String> lovedBeers) {
                assertEquals(lovedBeers.size(), 1);
                assertEquals(lovedBeers.get(0), "beer1");
            }
        };

        storage.setOnChangeListener(listener);
        storage.add("beer1");

        listener = new LovedBeersStorage.OnChangeListener() {
            @Override
            public void onChange(ArrayList<String> lovedBeers) {
                assertEquals(lovedBeers.size(), 2);
                assertEquals(lovedBeers.get(0), "beer1");
                assertEquals(lovedBeers.get(1), "beer2");
            }
        };

        storage.setOnChangeListener(listener);
        storage.add("beer2");

        listener = new LovedBeersStorage.OnChangeListener() {
            @Override
            public void onChange(ArrayList<String> lovedBeers) {
                assertEquals(lovedBeers.size(), 1);
                assertEquals(lovedBeers.get(0), "beer2");
            }
        };

        storage.setOnChangeListener(listener);
        storage.remove("beer1");

        listener = new LovedBeersStorage.OnChangeListener() {
            @Override
            public void onChange(ArrayList<String> lovedBeers) {
                assertEquals(lovedBeers.size(), 0);
            }
        };

        storage.setOnChangeListener(listener);
        storage.remove("beer2");
    }

}