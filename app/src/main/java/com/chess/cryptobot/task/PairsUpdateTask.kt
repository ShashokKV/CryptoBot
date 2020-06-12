package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.pairs.PairsHolder
import com.chess.cryptobot.enricher.PairResponseEnricher
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Pair

class PairsUpdateTask(pairsHolder: PairsHolder) : MarketTask<Pair, Pair>(pairsHolder) {
    private var enricher: PairResponseEnricher? = null

    override fun preMarketProcess(param: Pair) {
        enricher = PairResponseEnricher(param)
    }

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Pair): Pair {
        val response = market.getOrderBook(param.getPairNameForMarket(market.getMarketName()))
        synchronized(this) { enricher!!.enrichWithResponse(response)}
        return param
    }

    public override fun postMarketProcess(result: Pair?): Pair {
        return enricher!!.enrichWithMinPercent(null).pair
    }

    override fun exceptionProcess(param: Pair, exceptionMessage: String?): Pair {
        param.message = exceptionMessage
        return param
    }

    override fun doInPostExecute(result: Pair, holder: ContextHolder) {
        val pairsHolder = holder as PairsHolder
        if (result.percent < 0) {
            pairsHolder.remove(result)
            pairsHolder.addToNegativePercentPairs(result)
        } else {
            pairsHolder.setItem(result)
        }
    }

    override fun doInOnCanceled(result: Pair, holder: ContextHolder?) {
        val message = result.message ?: return
        val pairsHolder = holder as PairsHolder?
        if (message.contains("Unknown currency pair") || message.contains("INVALID_MARKET")) {
            pairsHolder!!.addToInvalidPairs(result)
        } else {
            pairsHolder!!.makeToast(message)
        }
        pairsHolder.remove(result)
    }
}