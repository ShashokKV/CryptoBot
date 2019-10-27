package com.chess.cryptobot.content.pairs;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.Preferences;

class PairsPreferences extends Preferences {
    PairsPreferences(Context context) {
        super(context);
    }

    @Override
    public String initPrefKey(Context context) {
        return context.getResources().getString(R.string.pairs_pref_key);
    }
}
