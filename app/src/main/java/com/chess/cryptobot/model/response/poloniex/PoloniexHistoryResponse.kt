package com.chess.cryptobot.model.response.poloniex

import com.chess.cryptobot.model.response.HistoryResponse
import java.time.*

class PoloniexHistoryResponse : HistoryResponse {
    var currencyPair: String? = null

    override var historyAction: String? = null

    var issueTime: LocalDateTime? = null

    override var historyPrice: Double? = null

    override var historyAmount: Double? = null

    var remainingAmount: Double? = null

    override val historyTime: ZonedDateTime?
        get() = ZonedDateTime.ofLocal(issueTime, ZoneId.of("Z"), ZoneOffset.UTC)

    override val historyName: String
        get() {
            return currencyPair!!.replace("_", "/")
        }

    override val historyMarket: String
        get() {
            return "poloniex"
        }

    override val progress: Int
        get() {
            if (historyAmount == 0.0) return 0
            return (((historyAmount!! - (remainingAmount)!!) / (historyAmount)!!) * 100).toInt()
        }
}