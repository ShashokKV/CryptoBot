package com.chess.cryptobot.market

import android.content.Context
import android.content.SharedPreferences
import com.chess.cryptobot.R

class WithdrawalMarketFactory: MarketFactory() {
    override val binanceApiKey = R.string.binance_withdrawal_api_key
    override val binanceSecretKey = R.string.binance_withdrawal_secret_key

    override fun binanceMarket(preferences: SharedPreferences, context: Context): BinanceMarketClient {
        val proxySelector = BinanceProxySelector(preferences.getString(context.getString(R.string.proxy_url), null),
                preferences.getString(context.getString(R.string.proxy_port), null),
                preferences.getString(context.getString(R.string.proxy_username), null),
                preferences.getString(context.getString(R.string.proxy_password), null))
        return BinanceMarketClient(context.getString(R.string.binance_url),
                preferences.getString(context.getString(binanceApiKey), null),
                preferences.getString(context.getString(binanceSecretKey), null),
                proxySelector)
    }
}