package com.chess.cryptobot.market

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.chess.cryptobot.R
import com.chess.cryptobot.util.SingletonHolder
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MarketFactory private constructor(context: Context) {
    private val bittrexApiKey = R.string.bittrex_api_key
    private val bittrexSecretKey = R.string.bittrex_secret_key
    private val binanceApiKey = R.string.binance_api_key
    private val binanceSecretKey = R.string.binance_secret_key
    private val binanceWithdrawalApiKey = R.string.binance_withdrawal_api_key
    private val binanceWithdrawalSecretKey = R.string.binance_withdrawal_secret_key
    private val livecoinApiKey = R.string.livecoin_api_key
    private val livecoinSecretKey = R.string.livecoin_secret_key

    private var binanceMarket: BinanceMarketClient
    private val binanceWithdrawalMarket: BinanceMarketClient
    private val bittrexMarket: BittrexMarketClient
    private val poloniexMarket: PoloniexMarketClient

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        binanceMarket = binanceMarket(preferences, context)
        binanceWithdrawalMarket = binanceWithdrawalMarket(preferences, context)
        bittrexMarket = bittrexMarket(preferences, context)
        poloniexMarket = livecoinMarket(preferences, context)
    }

    companion object : SingletonHolder<MarketFactory, Context>(::MarketFactory)

    fun getWithdrawalMarkets(): List<MarketClient?> {
        val marketNames = arrayOf(Market.BITTREX_MARKET, Market.BINANCE_WITHDRAWAL_MARKET, Market.POLONIEX_MARKET)
        return getMarkets(marketNames)
    }

    fun getMarkets(): List<MarketClient?> {
        val marketNames = arrayOf(Market.BITTREX_MARKET, Market.BINANCE_MARKET, Market.POLONIEX_MARKET)
        return getMarkets(marketNames)
    }

    private fun getMarkets(marketNames: Array<String>): List<MarketClient?> {
        val markets: MutableList<MarketClient?> = ArrayList()
        for (marketName in marketNames) {
            markets.add(getMarket(marketName))
        }
        return markets
    }

    private fun getMarket(marketName: String): MarketClient {

        return when (marketName) {
            Market.BITTREX_MARKET -> {
                bittrexMarket
            }
            Market.BINANCE_MARKET -> {
                binanceMarket
            }
            Market.BINANCE_WITHDRAWAL_MARKET -> {
                binanceWithdrawalMarket
            }
            Market.POLONIEX_MARKET -> {
                poloniexMarket
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

    private fun binanceMarket(preferences: SharedPreferences, context: Context): BinanceMarketClient {
        return BinanceMarketClient(context.getString(R.string.binance_url),
                preferences.getString(context.getString(binanceApiKey), null),
                preferences.getString(context.getString(binanceSecretKey), null),
                null)
    }

    private fun binanceWithdrawalMarket(preferences: SharedPreferences, context: Context): BinanceMarketClient {
        val proxySelector = BinanceProxySelector(preferences.getString(context.getString(R.string.proxy_url), null),
                preferences.getString(context.getString(R.string.proxy_port), null),
                preferences.getString(context.getString(R.string.proxy_username), null),
                preferences.getString(context.getString(R.string.proxy_password), null))
        return BinanceMarketClient(context.getString(R.string.binance_url),
                preferences.getString(context.getString(binanceWithdrawalApiKey), null),
                preferences.getString(context.getString(binanceWithdrawalSecretKey), null),
                proxySelector)
    }

    private fun livecoinMarket(preferences: SharedPreferences, context: Context): PoloniexMarketClient {
        return PoloniexMarketClient(context.getString(R.string.livecoin_url),
                preferences.getString(context.getString(livecoinApiKey), null),
                preferences.getString(context.getString(livecoinSecretKey), null))
    }

    object MarketHttpBuilder {
        val instance: OkHttpClient.Builder = OkHttpClient()
                .newBuilder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
    }
}