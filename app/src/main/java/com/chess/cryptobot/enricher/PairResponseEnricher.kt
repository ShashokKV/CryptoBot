package com.chess.cryptobot.enricher

import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.model.response.binance.BinanceOrderBookResponse
import com.chess.cryptobot.model.response.bittrex.BittrexResponse

class PairResponseEnricher(val pair: Pair) {
    private var bittrexResponse: OrderBookResponse? = null
    private var binanceResponse: OrderBookResponse? = null
    private var bids: List<Price?>? = null
    private var asks: List<Price?>? = null
    private var ask = 0.0
    private var bid = 0.0
    private var bidFromBinance = false
    private var minPercent = 0.0F
    private var bidQuantity = 0.0
    private var askQuantity = 0.0

    fun enrichWithResponse(response: OrderBookResponse) {
        if (response is BittrexResponse) {
            bittrexResponse = response
            pair.bittrexAsk = response.asks()?.get(0)?.value ?: 0.0
            pair.bittrexAskQuantity = response.asks()?.get(0)?.quantity ?: 0.0
            pair.bittrexBid = response.bids()?.get(0)?.value ?: 0.0
            pair.bittrexBidQuantity = response.bids()?.get(0)?.quantity ?: 0.0
        } else if (response is BinanceOrderBookResponse) {
            binanceResponse = response
            pair.binanceAsk = response.asks()?.get(0)?.value ?: 0.0
            pair.binanceAskQuantity = response.asks()?.get(0)?.quantity ?: 0.0
            pair.binanceBid = response.bids()?.get(0)?.value ?: 0.0
            pair.binanceBidQuantity = response.bids()?.get(0)?.quantity ?: 0.0
        }
    }

    fun enrichWithMinPercent(minPercent: Float): PairResponseEnricher {
        this.minPercent = minPercent
        val lAsk = binanceResponse?.asks()?.get(0)?.value ?: 0.0
        val lBid = binanceResponse?.bids()?.get(0)?.value ?: 0.0
        val bAsk = bittrexResponse?.asks()?.get(0)?.value ?: 0.0
        val bBid = bittrexResponse?.bids()?.get(0)?.value ?: 0.0
        if (lBid - bAsk > bBid - lAsk) {
            bid = lBid
            ask = bAsk
            bids = binanceResponse?.bids()
            asks = bittrexResponse?.asks()
            bidFromBinance = true
        } else {
            bid = bBid
            ask = lAsk
            bids = bittrexResponse?.bids()
            asks = binanceResponse?.asks()
            bidFromBinance = false
        }
        bidQuantity = bids?.get(0)?.quantity ?: 0.0
        askQuantity = asks?.get(0)?.quantity ?: 0.0
        val increaseAsks = bidQuantity > askQuantity
        val maxPriceSize = if (bids?.size ?: 0 > asks?.size ?: 0) asks?.size ?: 0 else bids?.size ?: 0
        for (i in 1 until maxPriceSize) {
            if (!enrichedFromStack(i, increaseAsks)) {
                return this
            }
        }
        return this
    }

    private fun enrichedFromStack(i: Int, increaseAsks: Boolean): Boolean {
        val percent = countPercent(bid, ask, bidFromBinance)
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

    private fun updatePair() {
        if (bidFromBinance) {
            pair.binanceBidQuantity = bidQuantity
            pair.bittrexAskQuantity = askQuantity
            pair.binanceBid = bid
            pair.bittrexAsk = ask
        } else {
            pair.bittrexBid = bidQuantity
            pair.binanceAskQuantity = askQuantity
            pair.bittrexBid = bid
            pair.binanceAsk = ask
        }
    }

    private fun countPercent(bid: Double?, ask: Double?, bidFromBinance: Boolean): Float {
        if (bid == null || ask == null) return 0.0f
        val bidFee = if (bidFromBinance) binanceFee else bittrexFee
        val askFee = if (bidFromBinance) bittrexFee else binanceFee
        val percentBid = (bid - bid / 100 * bidFee).toFloat()
        val percentAsk = (ask + ask / 100 * askFee).toFloat()
        return (percentBid - percentAsk) / percentBid * 100
    }

    fun countPercent(): PairResponseEnricher {
        if (pair.binanceAsk == 0.0 || pair.bittrexAsk == 0.0) {
            pair.percent = 0.0f
            return this
        }
        val bb = (pair.bittrexBid - pair.bittrexBid / 100 * bittrexFee).toFloat()
        val ba = (pair.bittrexAsk + pair.bittrexAsk / 100 * bittrexFee).toFloat()
        val lb = (pair.binanceBid - pair.binanceBid / 100 * binanceFee).toFloat()
        val la = (pair.binanceAsk + pair.binanceAsk / 100 * binanceFee).toFloat()
        val bittrexPercent = (bb - la) / la * 100
        val binancePercent = (lb - ba) / ba * 100
        if (bittrexPercent > binancePercent) {
            pair.percent = bittrexPercent
        } else {
            pair.percent = binancePercent
        }
        return this
    }

    companion object {
        private const val bittrexFee = 0.25
        private const val binanceFee = 0.18
    }

}