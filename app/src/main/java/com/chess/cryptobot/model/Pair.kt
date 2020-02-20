package com.chess.cryptobot.model

import java.io.Serializable

class Pair(val baseName: String, val marketName: String) : ViewItem, Serializable {
    var bittrexAsk = 0.0
    var bittrexAskQuantity = 0.0
    var bittrexBid = 0.0
    var bittrexBidQuantity = 0.0
    var bittrexVolume = 0.0
    var binanceAsk = 0.0
    var binanceAskQuantity = 0.0
    var binanceBid = 0.0
    var binanceBidQuantity = 0.0
    var binanceVolume = 0.0
    var percent = 0.0f
    var message: String? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other is Pair) {
            return other.marketName == marketName && other.baseName == baseName
        }
        return false
    }

    override val name: String
        get() = "$baseName/$marketName"

    fun getPairNameForMarket(marketName: String): String {
        return if (marketName == "bittrex") {
            bittrexPairName
        } else {
            binancePairName
        }
    }

    override fun hashCode(): Int {
        var result = baseName.hashCode()
        result = 31 * result + marketName.hashCode()
        return result
    }

    private val bittrexPairName: String
        get() = "$baseName-$marketName"

    private val binancePairName: String
        get() = "$marketName/$baseName"

    companion object {
        fun fromPairName(pairName: String): Pair {
            val coinNames = pairName.split("/").toTypedArray()
            return Pair(coinNames[0], coinNames[1])
        }
    }

}