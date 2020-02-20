package com.chess.cryptobot.content.balance

import android.content.Context
import com.chess.cryptobot.R
import com.chess.cryptobot.content.Preferences
import java.util.*

class BalancePreferences(context: Context?) : Preferences(context) {

    public override fun initPrefKey(context: Context?): String {
        return context?.resources?.getString(R.string.coin_names_pref_key) ?: "coinNames"
    }

    fun getMinBalance(coinName: String): Double {
        val minBalance = sharedPreferences.getString("min_$coinName", "0.0")
        return minBalance?.toDouble() ?: 0.0
    }

    internal fun setMinBalance(coinName: String, minBalance: Double) {
        val editor = sharedPreferences.edit()
        editor.putString("min_$coinName", String.format(Locale.US, "%.8f", minBalance))
        editor.apply()
    }
}
