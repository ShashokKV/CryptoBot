package com.chess.cryptobot.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public abstract class Preferences {
    private Context context;
    private SharedPreferences sharedPreferences;
    private String preferenceKey;

    public Preferences(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferenceKey = initPrefKey(context);
    }

    public abstract String initPrefKey(Context context);

    private String getPreferenceKey() {
        return preferenceKey;
    }

    public Set<String> getItemsSet() {
        return new HashSet<>(sharedPreferences.getStringSet(preferenceKey, new HashSet<>()));
    }

    void addItem(String itemName) {
        Set<String> itemsSet = getItemsSet();
        if (itemsSet.contains(itemName)) return;
        itemsSet.add(itemName);
        updateItemSet(itemsSet);
    }

    void removeItem(String itemName) {
        Set<String> itemsSet = getItemsSet();
        itemsSet.remove(itemName);
        updateItemSet(itemsSet);
    }

    private void updateItemSet(Set<String> itemsSet) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(getPreferenceKey(), itemsSet);
        editor.apply();
    }

    public String getValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    protected SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public Context getContext() {
        return context;
    }
}
