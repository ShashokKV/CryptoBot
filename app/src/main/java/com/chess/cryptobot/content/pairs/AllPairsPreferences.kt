package com.chess.cryptobot.content.pairs

import android.content.Context

import com.chess.cryptobot.R
import com.chess.cryptobot.content.Preferences

class AllPairsPreferences(context: Context) : Preferences(context) {

    public override fun initPrefKey(context: Context): String {
        return context.resources?.getString(R.string.available_pairs_pref_key) ?: "available_pairs"
    }
}
