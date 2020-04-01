package com.chess.cryptobot.util

import com.chess.cryptobot.exceptions.SyncServiceException
import com.chess.cryptobot.market.Market
import java.util.*

class CoinInfo(markets: List<Market?>) {
    private val statuses: MutableMap<String, Map<String?, Boolean?>> = HashMap()
    private val fees: MutableMap<String, Map<String?, Double?>> = HashMap()
    fun checkCoinStatus(coinName: String): Boolean {
        val bittrexStatuses = statuses[Market.BITTREX_MARKET] ?: return true
        val binanceStatuses = statuses[Market.BINANCE_MARKET] ?: return true
        val bittrexStatus = bittrexStatuses[coinName]
        val binanceStatus = binanceStatuses[coinName]
        return if (bittrexStatus == null || binanceStatus == null) true else bittrexStatus && binanceStatus
    }

    @Throws(SyncServiceException::class)
    fun getFee(marketName: String, coinName: String): Double {
        val fees = fees[marketName]
                ?: throw SyncServiceException("Can't get fees")
        return fees[coinName] ?: throw SyncServiceException("Can't get fee from $marketName")
    }

    init {
        for (market in markets) {
            if (market==null) break
            val currencies = market.getCurrencies()
            val fees: MutableMap<String?, Double?> = HashMap()
            val statuses: MutableMap<String?, Boolean?> = HashMap()
            for (currency in currencies) {
                val currencyName = currency.currencyName
                statuses[currencyName] = currency.isActive
                fees[currencyName] = currency.fee
            }

            this.statuses[market.getMarketName()] = statuses
            this.fees[market.getMarketName()] = fees
        }
    }
}