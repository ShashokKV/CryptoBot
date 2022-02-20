package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.CurrenciesResponse
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.TradeLimitResponse

interface Market {
    fun getMarketName(): String

    fun resetBalance()

    @Throws(MarketException::class)
    fun getAmount(coinName: String): Double

    @Throws(MarketException::class)
    fun getOrderBook(pairName: String): OrderBookResponse

    @Throws(MarketException::class)
    fun getTicker(): List<TickerResponse>

    @Throws(MarketException::class)
    fun getCurrencies(): List<CurrenciesResponse>

    @Throws(MarketException::class)
    fun getMinQuantity(): TradeLimitResponse?

    @Throws(MarketException::class)
    fun getAddress(coinName: String): String?

    @Throws(MarketException::class)
    fun sendCoins(coinName: String, amount: Double, address: String)

    @Throws(MarketException::class)
    fun buy(pairName: String, price: Double, amount: Double)

    @Throws(MarketException::class)
    fun sell(pairName: String, price: Double, amount: Double)

    fun keysIsEmpty(): Boolean
    @Throws(MarketException::class)
    fun getOpenOrders(): List<History>

    @Throws(MarketException::class)
    fun getHistory(context: Context): List<History>

    @Throws(MarketException::class)
    fun getDepositHistory(): List<History>

    @Throws(MarketException::class)
    fun getWithdrawHistory(): List<History>

    companion object {
        const val POLONIEX_MARKET = "poloniex"
        const val BITTREX_MARKET = "bittrex"
        const val BINANCE_MARKET = "binance"
        const val BINANCE_WITHDRAWAL_MARKET = "binanceWithdrawal"

    }
}