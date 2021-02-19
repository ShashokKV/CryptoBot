package com.chess.cryptobot.model

import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.market.Market.Companion.BITTREX_MARKET
import com.chess.cryptobot.market.Market.Companion.POLONIEX_MARKET
import com.chess.cryptobot.model.response.binance.BinanceDeserializer
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
    var minTradeSize = 0.0
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
            BITTREX_MARKET -> {
                bittrexPairName
            }
            BINANCE_MARKET -> {
                binancePairName
            }
            POLONIEX_MARKET -> {
                poloniexPairName
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
        get() = "$marketName-$baseName"

    private val binancePairName: String
        get() = "$marketName$baseName"

    private val poloniexPairName: String
        get() = baseName +"_$marketName"

    companion object {
        fun fromPairName(pairName: String): Pair {
            val coinNames = pairName.split("/").toTypedArray()
            return Pair(coinNames[0], coinNames[1])
        }

        fun normalizeFromMarketPairName(pairName: String, marketName: String): String {
            return when (marketName) {
                BINANCE_MARKET -> {
                    BinanceDeserializer.symbolToPairName(pairName)
                }
                BITTREX_MARKET -> {
                    val split = pairName.split("-")
                    split[1]+"/"+split[0]
                }
                else -> {
                    pairName.replace("_", "/")
                }
            }
        }
    }

}