package com.chess.cryptobot.util

import android.content.Context
import com.chess.cryptobot.exceptions.SyncServiceException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.room.CryptoBotDatabase

class MarketInfoReader(database: CryptoBotDatabase?) {
    private var coinInfoDao = database?.coinInfoDao
    private var minTradeSizeDao = database?.minTradeSizeDao

    constructor(context: Context): this(CryptoBotDatabase.getInstance(context))

    fun checkCoinStatus(coinName: String): Boolean {
        val bittrexStatus = coinInfoDao?.getByNameAndMarketName(coinName, Market.BITTREX_MARKET)?.status ?: true
        val binanceStatus = coinInfoDao?.getByNameAndMarketName(coinName, Market.BINANCE_MARKET)?.status ?: true
        val livecoinStatus = coinInfoDao?.getByNameAndMarketName(coinName, Market.POLONIEX_MARKET)?.status ?: true
        return bittrexStatus && binanceStatus && livecoinStatus
    }

    @Throws(SyncServiceException::class)
    fun getFee(marketName: String, coinName: String): Double {
        return coinInfoDao?.getByNameAndMarketName(coinName, marketName)?.fee?:0.0
    }

    fun getMinQuantity(pair: Pair): Double? {
        return minTradeSizeDao?.getByPairName(pair.name)?.minTradeSize
    }

    fun getStepSize(pair: Pair): Double? {
        return minTradeSizeDao?.getByPairName(pair.name)?.stepSize
    }

    fun getPriceFilter(pair: Pair): Double? {
        return minTradeSizeDao?.getByPairName(pair.name)?.priceFilter
    }
}