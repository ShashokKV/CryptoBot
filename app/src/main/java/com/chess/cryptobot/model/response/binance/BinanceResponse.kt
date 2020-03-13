package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.MarketResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToLong

open class BinanceResponse : MarketResponse {
    @SerializedName("success")
    @Expose
    private val success: Boolean? = null
    @SerializedName("msg")
    @Expose
    private val message: String? = null

    var responsesList: List<BinanceResponse>? = null

    override fun success(): Boolean {
        return success ?: true
    }

    override fun message(): String? {
        return message
    }

    protected fun symbolToPairName(symbol: String?) : String {
        if (symbol==null) return ""
        for (baseName in listOf("BTC","ETH","USDT")) {
            val index = symbol.indexOf(baseName, symbol.length - baseName.length)
            if (index>0) {
                return symbol.substring(0, index) + "/$baseName"
            }
        }
        return symbol
    }

    protected fun longToTime(longTime: Long) : LocalDateTime {
        return LocalDateTime.ofEpochSecond((longTime / 1000.toFloat()).roundToLong(), 0,
                ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
    }
}