package com.chess.cryptobot.model.response

import java.time.ZonedDateTime

interface HistoryResponse {
    val historyTime: ZonedDateTime?
    val historyName: String?
    val historyMarket: String?
    val historyAmount: Double?
    val historyPrice: Double?
    val historyAction: String?
    val progress: Int?
}