package com.chess.cryptobot.content.pairs;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.Preferences;

public class AllPairsPreferences extends Preferences {
    public AllPairsPreferences(Context context) {
        super(context);
    }

    @Override
    public String initPrefKey(Context context) {
        return context.getResources().getString(R.string.available_pairs_pref_key);
    }
}
