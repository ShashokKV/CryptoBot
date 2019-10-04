package com.chess.cryptobot.content;

import android.content.Context;
import android.content.SharedPreferences;

import com.chess.cryptobot.R;
import com.chess.cryptobot.activity.BalanceActivity;

import java.util.HashSet;
import java.util.Set;

public class Preferences {
    private BalanceActivity activity;
    private SharedPreferences sharedPref;

    public Preferences(BalanceActivity activity) {
        this.activity = activity;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public Set<String> getCoinNames() {
        return new HashSet<>(sharedPref.getStringSet(activity.getString(R.string.coin_names_pref), new HashSet<>()));
    }

    public void addCoinToBalance(String coinName) {
        Set<String> coinNames = getCoinNames();
        if (coinNames.contains(coinName)) return;
        coinNames.add(coinName);
        updateBalancePrefs(coinNames);
    }

    public void removeCoinFromBalance(String coinName) {
        Set<String> coinNames = getCoinNames();
        coinNames.remove(coinName);
        updateBalancePrefs(coinNames);
    }

    private void updateBalancePrefs(Set<String> coinNames) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(activity.getString(R.string.coin_names_pref), coinNames);
        editor.apply();
    }
}
