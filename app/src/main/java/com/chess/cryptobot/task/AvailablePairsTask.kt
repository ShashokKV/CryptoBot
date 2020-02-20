package com.chess.cryptobot.task

import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.pairs.PairsHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.response.TickerResponse
import java.util.*

class AvailablePairsTask(pairsHolder: PairsHolder) : MarketTask<Int, MutableList<String>?>(pairsHolder) {
    private var availablePairNames: MutableList<String>? = null
    private var bittrexVolumes: MutableMap<String, Double> = HashMap()
    private var binanceVolumes: MutableMap<String, Double> = HashMap()

    override fun preMarketProcess(param: Int) {}

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Int): MutableList<String>? {
        val pairNames: MutableList<String>
        val tickers = market.getTicker()
        pairNames = getPairNames(tickers)
        updateVolumesForMarket(market.getMarketName(), tickers)
        if (availablePairNames == null) {
            availablePairNames = LinkedList(pairNames)
        } else {
            availablePairNames?.retainAll(pairNames)
            binanceVolumes.keys.retainAll(availablePairNames!!)
            bittrexVolumes.keys.retainAll(availablePairNames!!)
        }
        return availablePairNames
    }

    private fun getPairNames(tickers: List<TickerResponse>): MutableList<String> {
        val pairNames: MutableList<String> = ArrayList()
        tickers.forEach { ticker: TickerResponse -> pairNames.add(ticker.marketName) }
        return pairNames
    }

    private fun updateVolumesForMarket(marketName: String, tickers: List<TickerResponse>) {
        if (marketName == Market.BITTREX_MARKET) {
            updateVolumes(bittrexVolumes, tickers)
        } else {
            updateVolumes(binanceVolumes, tickers)
        }
    }

    private fun updateVolumes(volumeMap: MutableMap<String, Double>, tickers: List<TickerResponse>) {
        tickers.forEach { ticker: TickerResponse -> volumeMap[ticker.marketName] = ticker.volume }
    }

    override fun postMarketProcess(result: MutableList<String>?): MutableList<String>? {
        return result
    }

    override fun exceptionProcess(param: Int, exceptionMessage: String?): MutableList<String>? {
        return null
    }

    override fun doInPostExecute(result: MutableList<String>?, holder: ContextHolder) {
        val pairsHolder = holder as PairsHolder
        pairsHolder.setAvailablePairs(excludePairs(result, holder))
        pairsHolder.setVolumes(bittrexVolumes, binanceVolumes)
        pairsHolder.removeInvalidPairs()
        pairsHolder.updateAllItems()
    }

    override fun doInOnCanceled(result: MutableList<String>?, holder: ContextHolder?) {}

    private fun excludePairs(allPairNames: MutableList<String>?, holder: ContextHolder?): MutableList<String>? {
        val context = holder!!.context
        val invalidPairs = listOf(*context!!.getString(R.string.ignored_pairs).split("#").toTypedArray())
        allPairNames?.removeAll(invalidPairs)
        val usdPairs: MutableList<String> = ArrayList()
        allPairNames?.forEach { marketName: String? -> if (marketName?.startsWith("USD/") == true) usdPairs.add(marketName) }
        allPairNames?.removeAll(usdPairs)
        return allPairNames
    }
}