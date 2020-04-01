package com.chess.cryptobot.market

import android.content.Context
import android.content.SharedPreferences
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import java.util.*

open class MarketFactory {
    open val bittrexApiKey = R.string.bittrex_api_key
    open val bittrexSecretKey = R.string.bittrex_secret_key
    open val binanceApiKey = R.string.binance_api_key
    open val binanceSecretKey = R.string.binance_secret_key


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
                bittrexMarket(preferences, context)
            }
            "binance" -> {
                binanceMarket(preferences, context)
            }
            else -> {
                throw IllegalArgumentException("Unknown market: $marketName")
            }
        }
    }

    fun bittrexMarket(preferences: SharedPreferences, context: Context): BittrexMarket {
        return BittrexMarket(context.getString(R.string.bittrex_url),
                preferences.getString(context.getString(bittrexApiKey), null),
                preferences.getString(context.getString(bittrexSecretKey), null))
    }

    fun binanceMarket(preferences: SharedPreferences, context: Context): BinanceMarket {
        val proxySelector = BinanceProxySelector(preferences.getString(context.getString(R.string.proxy_url), null),
                preferences.getString(context.getString(R.string.proxy_port), null),
                preferences.getString(context.getString(R.string.proxy_username), null),
                preferences.getString(context.getString(R.string.proxy_password), null))
        return BinanceMarket(context.getString(R.string.binance_url),
                preferences.getString(context.getString(binanceApiKey), null),
                preferences.getString(context.getString(binanceSecretKey), null),
                proxySelector)
    }
}