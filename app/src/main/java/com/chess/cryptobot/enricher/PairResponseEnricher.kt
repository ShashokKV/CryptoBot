package com.chess.cryptobot.enricher

import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse
import com.chess.cryptobot.model.response.bittrex.BittrexOrderBook
import com.chess.cryptobot.model.response.livecoin.LivecoinResponse

class PairResponseEnricher(val pair: Pair) {
    private var responsesMap = HashMap<String, OrderBookResponse>(3)
    private var bids: List<Price?>? = null
    private var asks: List<Price?>? = null
    private var bid = 0.0
    private var ask = Double.MAX_VALUE
    private var bidQuantity = 0.0
    private var askQuantity = 0.0
    private var minPercent: Float = 0.0F
    private var feeMap = hashMapOf(
            Market.BITTREX_MARKET to bittrexFee,
            Market.BINANCE_MARKET to binanceFee,
            Market.LIVECOIN_MARKET to livecoinFee)

    fun enrichWithResponse(response: OrderBookResponse) {
        var marketName  = ""
        when (response) {
            is BittrexOrderBook -> {
                marketName = Market.BITTREX_MARKET
            }
            is BinanceResponse -> {
                marketName = Market.BINANCE_MARKET
            }
            is LivecoinResponse -> {
                marketName = Market.LIVECOIN_MARKET
            }
        }
        initMaps(marketName, response)
    }

    fun enrichFromTicker(tickerResponse: TickerResponse, marketName: String): PairResponseEnricher {
        pair.askMap[marketName] = tickerResponse.tickerAsk
        pair.bidMap[marketName] = tickerResponse.tickerBid
        return this
    }

    private fun initMaps(marketName: String, response: OrderBookResponse) {
        responsesMap[marketName] = response
        pair.bidMap[marketName] = response.bids()?.get(0)?.value ?: 0.0
        pair.askMap[marketName] = response.asks()?.get(0)?.value ?: Double.MAX_VALUE
        pair.bidQuantityMap[marketName] = response.bids()?.get(0)?.quantity ?: 0.0
        pair.askQuantityMap[marketName] = response.asks()?.get(0)?.quantity ?: 0.0
    }

    fun enrichWithMinPercent(minPercent: Float?): PairResponseEnricher {
        bid = pair.bidMap.values.maxOrNull() ?:0.0
        ask = pair.askMap.values.minOrNull() ?:Double.MAX_VALUE
        pair.bidMarketName = pair.bidMap.filterValues {it == bid }.keys.first()
        pair.askMarketName = pair.askMap.filterValues { it == ask }.keys.first()
        bidQuantity = pair.bidQuantityMap[pair.bidMarketName]?:0.0
        askQuantity = pair.askQuantityMap[pair.askMarketName]?:0.0
        if (minPercent==null) {
            updatePair()
            pair.percent = countPercent()
            return this
        }
        this.minPercent = minPercent
        bids = responsesMap[pair.bidMarketName]?.bids()
        asks = responsesMap[pair.askMarketName]?.asks()

        val increaseAsks = bidQuantity > askQuantity
        val maxPriceSize = if (bids?.size ?: 0 > asks?.size ?: 0) asks?.size ?: 0 else bids?.size?: 0
        for (i in 1 until maxPriceSize) {
            if (!enrichedFromStack(i, increaseAsks)) {
                return this
            }
        }
        return this
    }

    private fun enrichedFromStack(i: Int, increaseAsks: Boolean): Boolean {
        val percent = countPercent()
        return if (percent > minPercent) {
            updatePair()
            pair.percent = percent
            if (increaseAsks) {
                askQuantity += asks?.get(i)?.quantity ?: 0.0
                ask = asks?.get(i)?.value ?: 0.0
            } else {
                bidQuantity += bids?.get(i)?.quantity ?: 0.0
                bid = bids?.get(i)?.value ?: 0.0
            }
            true
        } else {
            false
        }
    }

    private fun countPercent(): Float {
        val bidFee = feeMap[pair.bidMarketName]?:1.0
        val askFee = feeMap[pair.askMarketName]?:1.0
        val percentBid = (bid - bid / 100 * bidFee).toFloat()
        val percentAsk = (ask + ask / 100 * askFee).toFloat()
        return (percentBid - percentAsk) / percentBid * 100
    }

    private fun updatePair() {
        pair.bidMap[pair.bidMarketName] = bid
        pair.bidQuantityMap[pair.bidMarketName] = bidQuantity
        pair.askMap[pair.askMarketName] = ask
        pair.askQuantityMap[pair.askMarketName] = askQuantity
        pair.bid = bid
        pair.bidQuantity = bidQuantity
        pair.ask = ask
        pair.askQuantity = askQuantity
    }

    companion object {
        private const val bittrexFee = 0.2
        private const val binanceFee = 0.1
        private const val livecoinFee = 0.18
    }
}