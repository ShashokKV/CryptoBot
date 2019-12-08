package com.chess.cryptobot.content.history;

import android.content.Context;

import com.chess.cryptobot.content.Preferences;

public class HistoryPreferences extends Preferences {
    HistoryPreferences(Context context) {
        super(context);
    }

    @Override
    protected String initPrefKey(Context context) {
        return "history";
    }

    @Override
    public void addItem(String itemName) {
    }
}
