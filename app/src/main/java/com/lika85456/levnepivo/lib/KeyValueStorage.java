package com.lika85456.levnepivo.lib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple persistent key value storage for strings. Needs name identifier.
 *
 * <pre>
 * KeyValueStorage storage = new KeyValueStorage(this, "my_storage");
 * </pre>
 */
public class KeyValueStorage {
    protected final SharedPreferences preferences;

    public KeyValueStorage(Context context, String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
