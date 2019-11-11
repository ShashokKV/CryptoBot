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

    public Set<String> getItems() {
        return new HashSet<>(sharedPreferences.getStringSet(preferenceKey, new HashSet<>()));
    }

    public void setItems(Set<String> items) {
        updateItemSet(items);
    }

    void addItem(String itemName) {
        Set<String> itemsSet = getItems();
        if (itemsSet.contains(itemName)) return;
        itemsSet.add(itemName);
        updateItemSet(itemsSet);
    }

    void removeItem(String itemName) {
        Set<String> itemsSet = getItems();
        itemsSet.remove(itemName);
        updateItemSet(itemsSet);
    }

    private void updateItemSet(Set<String> itemsSet) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(getPreferenceKey(), itemsSet);
        editor.apply();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public Context getContext() {
        return context;
    }
}
