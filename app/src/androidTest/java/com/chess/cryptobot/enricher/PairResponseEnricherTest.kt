package com.chess.cryptobot.enricher

import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.Price
import org.junit.Test

class PairResponseEnricherTest {
    private val minPercent = 3
    private val pair = Pair.fromPairName("BTC/LRC")
    private var bids: List<Price> = ArrayList()
    private var asks: List<Price> = ArrayList()
    private var bid = 0.0
    private var ask = 0.0
    private var bidQuantity = 0.0
    private var askQuantity = 0.0

    @Test
    fun enrichWithMinPercent() {
        val lPrice1 = Price(0.00001017, 9.3313662)
        val lPrice2 = Price(0.00001016, 11.81102362)
        val lPrice3 = Price(0.00000843, 57.85761018)
        bids = listOf(lPrice1, lPrice2, lPrice3)


        val bPrice1 = Price(0.00000960, 200.0000)
        val bPrice2 = Price(0.00000961, 300.0000)
        val bPrice3 = Price(0.00000962, 400.0000)
        asks = listOf(bPrice1, bPrice2, bPrice3)

        pair.bidMap["poloniex"] = lPrice1.value
        pair.askMap["binance"] = bPrice1.value
        pair.bidQuantityMap["poloniex"] = lPrice1.quantity
        pair.askQuantityMap["binance"] = bPrice1.quantity

        enrichWithMinPercentTest()

    }

    private fun enrichWithMinPercentTest(): PairResponseEnricherTest {
        bid = pair.bidMap.values.maxOrNull() ?:0.0
        ask = pair.askMap.values.minOrNull() ?:Double.MAX_VALUE
        pair.bidMarketName = pair.bidMap.filterValues {it == bid }.keys.first()
        pair.askMarketName = pair.askMap.filterValues { it == ask }.keys.first()
        bidQuantity = pair.bidQuantityMap[pair.bidMarketName]?:0.0
        askQuantity = pair.askQuantityMap[pair.askMarketName]?:0.0

        val increaseAsks = pair.bidQuantity > pair.askQuantity
        val maxPriceSize = if (bids.size > asks.size) asks.size else bids.size
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
                askQuantity += asks[i].quantity
                ask = asks[i].value
            } else {
                bidQuantity += bids[i].quantity
                bid = bids[i].value
            }
            true
        } else {
            false
        }
    }

    private fun countPercent(): Float {
        val feeMap = HashMap<String, Double>()
        feeMap["binance"] = 0.18
        feeMap["poloniex"] = 0.25
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
        pair.askQuantity
    }
}

