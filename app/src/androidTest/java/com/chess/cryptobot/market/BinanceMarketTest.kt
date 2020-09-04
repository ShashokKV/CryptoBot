package com.chess.cryptobot.market

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import com.chess.cryptobot.R
import org.junit.Test

class BinanceMarketTest {
    private val mContext: Context = ApplicationProvider.getApplicationContext()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
    private val binanceApiKey = R.string.binance_api_key
    private val binanceSecretKey = R.string.binance_secret_key
    private var binanceMarket = BinanceMarket(mContext.getString(R.string.binance_url),
            preferences.getString(mContext.getString(binanceApiKey), null),
            preferences.getString(mContext.getString(binanceSecretKey), null),
            null)

    @Test
    fun getAddress() {
        val address = binanceMarket.getAddress("VET")
        println(address)
        assert(address!=null && address.isNotEmpty())
    }
}