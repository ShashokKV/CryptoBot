package com.chess.cryptobot.content.history

import android.content.Context
import com.chess.cryptobot.R

import com.chess.cryptobot.content.Preferences

class HistoryPreferences internal constructor(context: Context?) : Preferences(context) {

    override fun initPrefKey(context: Context?): String {
        return context?.resources?.getString(R.string.history_pref_key) ?: "history"
    }

    override fun addItem(itemName: String) {}
}
