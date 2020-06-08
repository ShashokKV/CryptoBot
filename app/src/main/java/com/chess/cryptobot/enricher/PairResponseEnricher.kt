package com.chess.cryptobot.enricher

import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse
import com.chess.cryptobot.model.response.bittrex.BittrexResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinResponse

class PairResponseEnricher(val pair: Pair) {
    private var responsesMap = HashMap<String, OrderBookResponse>(3)
    private var bids: List<Price?>? = null
    private var asks: List<Price?>? = null
    private var minPercent: Float = 0.0F
    private var feeMap = hashMapOf(
            Market.BITTREX_MARKET to bittrexFee,
            Market.BINANCE_MARKET to binanceFee,
            Market.LIVECOIN_MARKET to livecoinFee)

    fun enrichWithResponse(response: OrderBookResponse) {
        var marketName  = ""
        when (response) {
            is BittrexResponse -> {
                marketName = Market.BITTREX_MARKET
            }
            is BinanceResponse -> {
                marketName = Market.BINANCE_MARKET
            }
            is LivecoinResponse -> {
                marketName = Market.LIVECOIN_MARKET
            }
        }
        initMaps(marketName,
                response,
                response.bids()?.get(0)?.value ?: 0.0,
                response.bids()?.get(0)?.quantity ?: 0.0,
                response.asks()?.get(0)?.value ?: 0.0,
                response.asks()?.get(0)?.quantity ?: 0.0)
    }

    fun enrichFromTicker(tickerResponse: TickerResponse, marketName: String) {
        pair.askMap[marketName] = tickerResponse.tickerAsk!!
        pair.bidMap[marketName] = tickerResponse.tickerBid!!
        pair.volumeMap[marketName] = tickerResponse.volume
    }

    private fun initMaps(marketName: String, response: OrderBookResponse, bid: Double, bidQuantity: Double, ask: Double, askQuantity: Double) {
        responsesMap[marketName] = response
        pair.bidMap[marketName] = bid
        pair.askMap[marketName] = ask
        pair.bidQuantityMap[marketName] = bidQuantity
        pair.askQuantityMap[marketName] = askQuantity
    }

    fun enrichWithMinPercent(minPercent: Float?): PairResponseEnricher {
        pair.bid = pair.bidMap.values.max()?:0.0
        pair.ask = pair.askMap.values.min()?:0.0
        pair.bidMarketName = pair.bidMap.filterValues {it == pair.bid }.keys.first()
        pair.askMarketName = pair.askMap.filterValues { it == pair.ask }.keys.first()
        if (minPercent==null) {
            countPercent()
            return this
        }
        this.minPercent = minPercent
        pair.bidQuantity = pair.bidQuantityMap[pair.bidMarketName]?:0.0
        pair.askQuantity = pair.askQuantityMap[pair.askMarketName]?:0.0
        bids = responsesMap[pair.bidMarketName]?.bids()
        asks = responsesMap[pair.askMarketName]?.asks()

        val increaseAsks = pair.bidQuantity > pair.askQuantity
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
                pair.askQuantity += asks?.get(i)?.quantity ?: 0.0
                pair.ask = asks?.get(i)?.value ?: 0.0
            } else {
                pair.bidQuantity += bids?.get(i)?.quantity ?: 0.0
                pair.bid = bids?.get(i)?.value ?: 0.0
            }
            true
        } else {
            false
        }
    }

    private fun countPercent(): Float {
        val bidFee = feeMap[pair.bidMarketName]?:1.0
        val askFee = feeMap[pair.askMarketName]?:1.0
        val percentBid = (pair.bid - pair.bid / 100 * bidFee).toFloat()
        val percentAsk = (pair.ask + pair.ask / 100 * askFee).toFloat()
        return (percentBid - percentAsk) / percentBid * 100
    }

    private fun updatePair() {
        pair.bidMap[pair.bidMarketName] = pair.bid
        pair.bidQuantityMap[pair.bidMarketName] = pair.bidQuantity
        pair.askMap[pair.askMarketName] = pair.ask
        pair.askQuantityMap[pair.askMarketName] = pair.askQuantity
    }

    companion object {
        private const val bittrexFee = 0.25
        private const val binanceFee = 0.18
        private const val livecoinFee = 0.25
    }
}