package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.BittrexMarket
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.response.CurrenciesResponse
import com.chess.cryptobot.model.response.bittrex.BittrexGenericResponse
import com.chess.cryptobot.model.response.bittrex.BittrexResponse
import java.util.*

class CoinStatusTask(holder: ContextHolder?) : MarketTask<Int, Int>(holder!!) {
    private var bittrexStatuses: MutableMap<String, Boolean> = HashMap()
    private var binanceStatuses: MutableMap<String, Boolean> = HashMap()
    private var coinIcons: MutableMap<String, String> = HashMap()

    override fun preMarketProcess(param: Int) {
    }

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Int): Int {
        if (market.getMarketName() == Market.BITTREX_MARKET) {
            updateStatuses(bittrexStatuses, market.getCurrencies())
            val bittrexMarket = market as BittrexMarket
            updateIcons(bittrexMarket.markets)
        } else if (market.getMarketName() == Market.BINANCE_MARKET) {
            updateStatuses(binanceStatuses, market.getCurrencies())
        }
        return 0
    }

    private fun updateStatuses(statusMap: MutableMap<String, Boolean>, currencies: List<CurrenciesResponse>) {
        currencies.forEach{ currency: CurrenciesResponse -> statusMap[currency.currencyName!!] = currency.isActive?: true}
    }

    private fun updateIcons(response: BittrexResponse) {
       response.results.forEach { result: BittrexGenericResponse? -> if (result!=null) coinIcons[result.marketCurrency!!] = result.logoUrl!! }
    }

    public override fun postMarketProcess(result: Int?): Int {
        return 0
    }

    override fun exceptionProcess(param: Int, exceptionMessage: String?): Int {
        return 0
    }

    override fun doInPostExecute(result: Int, holder: ContextHolder) {
        val balanceHolder = holder as BalanceHolder
        balanceHolder.setCurrencyStatus(bittrexStatuses, binanceStatuses)
        balanceHolder.setIconUrls(coinIcons)
        balanceHolder.updateAllItems()
    }

    override fun doInOnCanceled(result: Int, holder: ContextHolder?) {}
}