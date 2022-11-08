package com.lika85456.levnepivo.lib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple key value storage for strings.
 *
 * @example From an activity
 * KeyValueStorage storage = new KeyValueStorage(this, "my_storage");
 *
 */
public class KeyValueStorage {
    protected SharedPreferences preferences;

    public KeyValueStorage(Context context, String name){
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void set(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key, String defaultValue){
        return preferences.getString(key, defaultValue);
    }

    public void remove(String key){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
