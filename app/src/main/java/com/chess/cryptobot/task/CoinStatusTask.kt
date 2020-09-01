package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.BittrexMarket
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.response.CurrenciesResponse
import com.chess.cryptobot.model.response.bittrex.BittrexCurrency
import java.util.*
import kotlin.collections.ArrayList

class CoinStatusTask(holder: ContextHolder?) : MarketTask<Int, Int>(holder!!) {
    private var bittrexStatuses: MutableMap<String, Boolean> = HashMap()
    private var binanceStatuses: MutableMap<String, Boolean> = HashMap()
    private var livecoinStatuses: MutableMap<String, Boolean> = HashMap()
    private var coinIcons: MutableMap<String, String> = HashMap()
    private var availableCoins = ArrayList<String>()

    override fun preMarketProcess(param: Int) {
    }

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Int): Int {
        when {
            market.getMarketName() == Market.BITTREX_MARKET -> {
                updateStatuses(bittrexStatuses, market.getCurrencies())
                val bittrexMarket = market as BittrexMarket
                updateIcons(bittrexMarket.getCurrencies())
            }
            market.getMarketName() == Market.BINANCE_MARKET -> {
                updateStatuses(binanceStatuses, market.getCurrencies())
            }
            market.getMarketName() == Market.LIVECOIN_MARKET -> {
                updateStatuses(livecoinStatuses, market.getCurrencies())
            }
        }
        return 0
    }

    @Synchronized
    private fun updateStatuses(statusMap: MutableMap<String, Boolean>, currencies: List<CurrenciesResponse>) {
        val marketCoins = ArrayList<String>()
        currencies.forEach{ currency: CurrenciesResponse ->
            statusMap[currency.currencyName!!] = currency.isActive?: true
            marketCoins.add(currency.currencyName!!)
        }
        if (availableCoins.isEmpty()) {
            availableCoins.addAll(marketCoins)
        } else {
            availableCoins.retainAll(marketCoins)
        }
    }

    private fun updateIcons(response: List<CurrenciesResponse>) {
       response.forEach { result: CurrenciesResponse ->
           result as BittrexCurrency
           coinIcons[result.currencyName!!] = result.logoUrl!!
       }
    }

    public override fun postMarketProcess(result: Int?): Int {
        return 0
    }

    override fun exceptionProcess(param: Int, exceptionMessage: String?): Int {
        return 0
    }

    override fun doInPostExecute(result: Int, holder: ContextHolder) {
        val balanceHolder = holder as BalanceHolder
        balanceHolder.setCurrencyStatus(bittrexStatuses, binanceStatuses, livecoinStatuses)
        balanceHolder.setIconUrls(coinIcons)
        balanceHolder.availableCoins = availableCoins
        balanceHolder.updateAllItems()
    }

    override fun doInOnCanceled(result: Int, holder: ContextHolder?) {}
}