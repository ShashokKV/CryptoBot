package com.chess.cryptobot.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.chess.cryptobot.R;

import java.util.Set;
import java.util.TreeSet;

public class Preferences {
    private Context context;
    private SharedPreferences sharedPreferences;

    Preferences(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    Set<String> getCoinNames() {
        return new TreeSet<>(sharedPreferences.getStringSet(context.getString(R.string.coin_names), new TreeSet<>()));
    }

    void addCoinToBalance(String coinName) {
        Set<String> coinNames = getCoinNames();
        if (coinNames.contains(coinName)) return;
        coinNames.add(coinName);
        updateBalancePrefs(coinNames);
    }

    void removeCoinFromBalance(String coinName) {
        Set<String> coinNames = getCoinNames();
        coinNames.remove(coinName);
        updateBalancePrefs(coinNames);
    }

    private void updateBalancePrefs(Set<String> coinNames) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(context.getString(R.string.coin_names), coinNames);
        editor.apply();
    }
}
