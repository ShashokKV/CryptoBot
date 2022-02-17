package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.pairs.PairsHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.response.TickerResponse

class AvailablePairsTask(pairsHolder: PairsHolder) :
    MarketTask<Int, MutableList<String>?>(pairsHolder) {
    private var availablePairNames: MutableList<String>? = null

    override fun preMarketProcess(param: Int) {}

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Int): MutableList<String>? {
        val tickers = market.getTicker()
        val pairNames = getPairNames(tickers)
        synchronized(this) {
            if (availablePairNames == null) {
                availablePairNames = ArrayList(pairNames)
            } else {
                availablePairNames?.retainAll(pairNames)
            }
        }

        return availablePairNames
    }

    private fun getPairNames(tickers: List<TickerResponse>): MutableList<String> {
        val pairNames: MutableList<String> = ArrayList()
        tickers.forEach { ticker: TickerResponse -> pairNames.add(ticker.tickerName) }
        return pairNames
    }

    override fun postMarketProcess(result: MutableList<String>?): MutableList<String>? {
        return result
    }

    override fun exceptionProcess(param: Int, exceptionMessage: String?): MutableList<String>? {
        return null
    }

    override fun doInPostExecute(result: MutableList<String>?, holder: ContextHolder) {
        val pairsHolder = holder as PairsHolder
        pairsHolder.setAvailablePairs(result)
        pairsHolder.hasAvailablePairs = true
        pairsHolder.removeInvalidPairs()
        pairsHolder.updateAllItems()
    }

    override fun doInOnCanceled(result: MutableList<String>?, holder: ContextHolder?) {}

}