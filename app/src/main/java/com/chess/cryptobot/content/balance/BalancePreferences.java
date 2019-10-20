package com.chess.cryptobot.content.balance;

import android.content.Context;
import android.content.SharedPreferences;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.Preferences;

import java.util.Objects;
import java.util.Set;

public class BalancePreferences extends Preferences {
    BalancePreferences(Context context) {
        super(context);
    }

    public Double getMinBalance(String coinName) {
        return Double.parseDouble(Objects.requireNonNull(getSharedPreferences().getString("min_".concat(coinName),"0.0")));
    }

    void setMinBalance(String coinName, Double minBalance) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("min_".concat(coinName), minBalance.toString());
        editor.apply();
    }

    void addCoinToBalance(String coinName) {
        Set<String> coinNames = getCoinNames();
        if (coinNames.contains(coinName)) return;
        coinNames.add(coinName);
        updateBalancePrefs(coinNames);
    }

    void removeCoin(String coinName) {
        Set<String> coinNames = getCoinNames();
        coinNames.remove(coinName);
        updateBalancePrefs(coinNames);
    }

    private void updateBalancePrefs(Set<String> coinNames) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(getContext().getString(R.string.coin_names), coinNames);
        editor.apply();
    }
}
