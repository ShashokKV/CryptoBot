package com.chess.cryptobot.market

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import com.chess.cryptobot.R
import org.junit.Test

class BittrexMarketTest {
    private val mContext: Context = ApplicationProvider.getApplicationContext()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
    private val bittrexApiKey = R.string.bittrex_api_key
    private val bittrexSecretKey = R.string.bittrex_secret_key
    private var bittrexMarket = BittrexMarketClient(mContext.getString(R.string.bittrex_url),
            preferences.getString(mContext.getString(bittrexApiKey), null),
            preferences.getString(mContext.getString(bittrexSecretKey), null))

    @Test
    fun buy() {
        //bittrexMarket.buy("GNT-BTC", 0.00000800, 75.0)
    }

    @Test
    fun sendCoins() {
//        bittrexMarket.sendCoins("VET", 600.0, "0x85fb3d027382ee60f0d3e2bfaf9eab7efed810cb")
    }
}