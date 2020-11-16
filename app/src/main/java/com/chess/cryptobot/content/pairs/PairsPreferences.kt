package com.chess.cryptobot.content.pairs

import android.content.Context

import com.chess.cryptobot.R
import com.chess.cryptobot.content.Preferences

internal class PairsPreferences(context: Context) : Preferences(context) {

    public override fun initPrefKey(context: Context): String {
        return context.resources?.getString(R.string.pairs_pref_key) ?: "pairs"
    }
}
