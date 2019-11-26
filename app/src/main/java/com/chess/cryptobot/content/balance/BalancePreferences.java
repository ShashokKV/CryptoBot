package com.chess.cryptobot.content.balance;

import android.content.Context;
import android.content.SharedPreferences;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.Preferences;

import java.util.Objects;

public class BalancePreferences extends Preferences {
    public BalancePreferences(Context context) {
        super(context);
    }

    @Override
    public String initPrefKey(Context context) {
        return context.getResources().getString(R.string.coin_names_pref_key);
    }

    public Double getMinBalance(String coinName) {
        return Double.parseDouble(Objects.requireNonNull(getSharedPreferences().getString("min_".concat(coinName), "0.0")));
    }

    void setMinBalance(String coinName, Double minBalance) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("min_".concat(coinName), minBalance.toString());
        editor.apply();
    }
}
