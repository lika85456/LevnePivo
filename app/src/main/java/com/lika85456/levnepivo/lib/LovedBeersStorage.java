package com.lika85456.levnepivo.lib;

import android.content.Context;

import java.util.ArrayList;

public class LovedBeersStorage {
    private static final String KEY = "loved_beers";
    private static final String STORAGE_NAME = "loved_beers_storage";
    private final KeyValueStorage storage;
    private final ArrayList<String> lovedBeers;
    // on change listener
    private OnChangeListener onChangeListener;

    public LovedBeersStorage(Context context) {
        storage = new KeyValueStorage(context, STORAGE_NAME);
        lovedBeers = new ArrayList<>();
        String[] beers = storage.get(KEY, "").split(",");
        for (String beer : beers) {
            if (!beer.isEmpty()) {
                lovedBeers.add(beer);
            }
        }
    }

    public ArrayList<String> getLovedBeers() {
        return lovedBeers;
    }

    public void add(String beerName) {
        // check if beer is already loved
        if (lovedBeers.contains(beerName)) {
            return;
        }

        lovedBeers.add(beerName);
        save();

        if (onChangeListener != null) {
            onChangeListener.onChange(getLovedBeers());
        }
    }

    public void remove(String beerName) {
        lovedBeers.remove(beerName);
        save();

        if (onChangeListener != null) {
            onChangeListener.onChange(getLovedBeers());
        }
    }

    public boolean isLoved(String beerName) {
        return lovedBeers.contains(beerName);
    }

    private void save() {
        String[] beers = new String[lovedBeers.size()];
        beers = lovedBeers.toArray(beers);
        storage.set(KEY, String.join(",", beers));
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void clear() {
        lovedBeers.clear();
        save();
    }

    public interface OnChangeListener {
        void onChange(ArrayList<String> lovedBeers);
    }
}
