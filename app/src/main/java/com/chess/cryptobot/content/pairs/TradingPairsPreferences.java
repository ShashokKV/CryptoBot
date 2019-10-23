package com.chess.cryptobot.content.pairs;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.Preferences;

class TradingPairsPreferences extends Preferences {
    TradingPairsPreferences(Context context) {
        super(context);
    }

    @Override
    public String initPrefKey(Context context) {
        return context.getResources().getString(R.string.trading_pairs_pref_key);
    }
}
