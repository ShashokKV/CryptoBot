package com.chess.cryptobot.content.balance

import android.content.Context
import com.chess.cryptobot.R
import com.chess.cryptobot.content.Preferences

class BalancePreferences(context: Context?) : Preferences(context) {
    private var minBtcKey: String = "min_btc_amount"
    private var minEthKey: String = "min_eth_amount"
    private var minUsdtKey: String = "min_usdt_amount"

    public override fun initPrefKey(context: Context?): String {
        minBtcKey = context?.resources?.getString(R.string.min_btc_amount) ?: "min_btc_amount"
        minEthKey = context?.resources?.getString(R.string.min_eth_amount) ?: "min_eth_amount"
        minUsdtKey = context?.resources?.getString(R.string.min_usdt_amount) ?: "min_usdt_amount"
        return context?.resources?.getString(R.string.coin_names_pref_key) ?: "coinNames"
    }

    fun getMinBtcAmount(): Double {
        return sharedPreferences.getString(minBtcKey, "0.0005")?.toDouble() ?: 0.0005
    }

    fun getMinEthAmount(): Double {
        return sharedPreferences.getString(minEthKey, "0.025")?.toDouble() ?: 0.025
    }

    fun getMinUsdtAmount(): Double {
        return sharedPreferences.getString(minUsdtKey, "10.0")?.toDouble() ?: 10.0
    }
}
