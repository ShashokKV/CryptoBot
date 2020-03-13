package com.chess.cryptobot.market

import android.content.Context
import android.content.SharedPreferences
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import java.util.*

class MarketFactory {
    fun getMarkets(contextHolder: ContextHolder): List<Market?> {
        return getMarkets(contextHolder.context, contextHolder.prefs.sharedPreferences)
    }

    fun getMarkets(context: Context?, preferences: SharedPreferences): List<Market?> {
        val marketNames = arrayOf("bittrex", "binance")
        val markets: MutableList<Market?> = ArrayList()
        for (marketName in marketNames) {
            markets.add(getMarket(marketName, preferences, context))
        }
        return markets
    }

    private fun getMarket(marketName: String, preferences: SharedPreferences, context: Context?): Market? {
        if (context == null) return null
        return when (marketName) {
            "bittrex" -> {
                BittrexMarket(context.getString(R.string.bittrex_url),
                        preferences.getString(context.getString(R.string.bittrex_api_key), null),
                        preferences.getString(context.getString(R.string.bittrex_secret_key), null))
            }
            "binance" -> {
                val proxySelector = BinanceProxySelector(preferences.getString(context.getString(R.string.proxy_url), null),
                        preferences.getString(context.getString(R.string.proxy_port), null),
                        preferences.getString(context.getString(R.string.proxy_username), null),
                        preferences.getString(context.getString(R.string.proxy_password), null))
                BinanceMarket(context.getString(R.string.binance_url),
                        preferences.getString(context.getString(R.string.binance_api_key), null),
                        preferences.getString(context.getString(R.string.binance_secret_key), null),
                        proxySelector)

            }
            else -> {
                throw IllegalArgumentException("Unknown market: $marketName")
            }
        }
    }
}