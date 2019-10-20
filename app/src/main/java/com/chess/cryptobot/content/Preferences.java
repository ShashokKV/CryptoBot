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

    public Preferences(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected Set<String> getCoinNames() {
        return new TreeSet<>(sharedPreferences.getStringSet(getContext().getString(R.string.coin_names), new TreeSet<>()));
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
