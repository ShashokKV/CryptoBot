package com.chess.cryptobot.market

import android.content.Context
import android.content.SharedPreferences
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder

open class MarketFactory {
    open val bittrexApiKey = R.string.bittrex_api_key
    open val bittrexSecretKey = R.string.bittrex_secret_key
    open val binanceApiKey = R.string.binance_api_key
    open val binanceSecretKey = R.string.binance_secret_key
    open val livecoinApiKey = R.string.livecoin_api_key
    open val livecoinSecretKey = R.string.livecoin_secret_key


    fun getMarkets(contextHolder: ContextHolder): List<Market?> {
        return getMarkets(contextHolder.context, contextHolder.prefs.sharedPreferences)
    }

    fun getMarkets(context: Context?, preferences: SharedPreferences): List<MarketClient?> {
        val marketNames = arrayOf(Market.BITTREX_MARKET, Market.BINANCE_MARKET, Market.LIVECOIN_MARKET)
        val markets: MutableList<MarketClient?> = ArrayList()
        for (marketName in marketNames) {
            markets.add(getMarket(marketName, preferences, context))
        }
        return markets
    }

    private fun getMarket(marketName: String, preferences: SharedPreferences, context: Context?): MarketClient? {
        if (context == null) return null
        return when (marketName) {
            Market.BITTREX_MARKET -> {
                bittrexMarket(preferences, context)
            }
            Market.BINANCE_MARKET -> {
                binanceMarket(preferences, context)
            }
            Market.LIVECOIN_MARKET -> {
                livecoinMarket(preferences, context)
            }
            else -> {
                throw IllegalArgumentException("Unknown market: $marketName")
            }
        }
    }

    private fun bittrexMarket(preferences: SharedPreferences, context: Context): BittrexMarketClient {
        return BittrexMarketClient(context.getString(R.string.bittrex_url),
                preferences.getString(context.getString(bittrexApiKey), null),
                preferences.getString(context.getString(bittrexSecretKey), null))
    }

    open fun binanceMarket(preferences: SharedPreferences, context: Context): BinanceMarketClient {
        return BinanceMarketClient(context.getString(R.string.binance_url),
                preferences.getString(context.getString(binanceApiKey), null),
                preferences.getString(context.getString(binanceSecretKey), null),
                null)
    }

    private fun livecoinMarket(preferences: SharedPreferences, context: Context): LivecoinMarketClient {
        return LivecoinMarketClient(context.getString(R.string.livecoin_url),
                preferences.getString(context.getString(livecoinApiKey), null),
                preferences.getString(context.getString(livecoinSecretKey), null))
    }
}