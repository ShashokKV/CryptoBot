package com.chess.cryptobot.model

import android.graphics.Bitmap
import com.chess.cryptobot.market.Market

class Balance(override val name: String) : ViewItem {
    var coinUrl: String? = null
    var coinIcon: Bitmap? = null
    private val amounts: MutableMap<String, Double>
    var message: String? = null
    private val statuses: MutableMap<String, Boolean> = HashMap()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        return if (other is Balance) {
            other.name == name
        } else {
            false
        }
    }

    fun setAmount(marketName: String, amount: Double) {
        amounts[marketName] = amount
    }

    fun getAmount(marketName: String?): Double {
        return amounts[marketName] ?: return 0.0
    }

    fun getAmounts(): Map<String, Double> {
        return amounts
    }

    fun setStatuses(binanceStatus: Boolean, bittrexStatus: Boolean, livecoinStatus: Boolean) {
        statuses[Market.BINANCE_MARKET] = binanceStatus
        statuses[Market.BITTREX_MARKET] = bittrexStatus
        statuses[Market.LIVECOIN_MARKET] = livecoinStatus
    }

    fun getStatus(marketName: String?): Boolean {
        return statuses[marketName] ?: return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    init {
        amounts = HashMap()
        amounts[Market.BITTREX_MARKET] = 0.0
        amounts[Market.BINANCE_MARKET] = 0.0
        amounts[Market.LIVECOIN_MARKET] = 0.0
        statuses[Market.BITTREX_MARKET] = true
        statuses[Market.BINANCE_MARKET] = true
        statuses[Market.LIVECOIN_MARKET] = true
    }
}