package com.chess.cryptobot.model

import com.chess.cryptobot.market.Market
import java.io.Serializable

class Pair(val baseName: String, val marketName: String) : ViewItem, Serializable {
    var ask = 0.0
    var bid = 0.0
    var askQuantity = 0.0
    var bidQuantity = 0.0
    var bidMarketName = ""
    var askMarketName = ""
    var askMap = HashMap<String, Double>()
    var askQuantityMap = HashMap<String, Double>()
    var bidMap = HashMap<String, Double>()
    var bidQuantityMap = HashMap<String, Double>()
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
        return when(marketName) {
            Market.BITTREX_MARKET -> {
                bittrexPairName
            }
            Market.BINANCE_MARKET -> {
                binancePairName
            }
            Market.LIVECOIN_MARKET -> {
                livecoinPairName
            }
            else -> {
                throw IllegalArgumentException("Unknown market: $marketName")
            }
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
        get() = "$marketName$baseName"

    private val livecoinPairName: String
        get() = "$marketName/$baseName"

    companion object {
        fun fromPairName(pairName: String): Pair {
            val coinNames = pairName.split("/").toTypedArray()
            return Pair(coinNames[0], coinNames[1])
        }
    }

}