package com.chess.cryptobot.util

import android.content.Context
import com.chess.cryptobot.exceptions.SyncServiceException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.room.CryptoBotDatabase

class MarketInfoReader(context: Context) {
    private val database = CryptoBotDatabase.getInstance(context)
    private val coinInfoDao = database?.coinInfoDao
    private val minTradeSizeDao = database?.minTradeSizeDao

    fun checkCoinStatus(coinName: String): Boolean {
        val bittrexStatus = coinInfoDao?.getByNameAndMarketName(coinName, Market.BITTREX_MARKET)?.status ?: true
        val binanceStatus = coinInfoDao?.getByNameAndMarketName(coinName, Market.BINANCE_MARKET)?.status ?: true
        val livecoinStatus = coinInfoDao?.getByNameAndMarketName(coinName, Market.LIVECOIN_MARKET)?.status ?: true
        return bittrexStatus && binanceStatus && livecoinStatus
    }

    @Throws(SyncServiceException::class)
    fun getFee(marketName: String, coinName: String): Double {
        return coinInfoDao?.getByNameAndMarketName(coinName, marketName)?.fee?:0.0
    }

    fun getMinQuantity(pair: Pair): Double? {
        return minTradeSizeDao?.getByPairName(pair.name)?.minTradeSize
    }
}