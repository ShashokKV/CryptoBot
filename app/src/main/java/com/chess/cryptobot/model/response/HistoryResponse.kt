package com.chess.cryptobot.model.response

import java.time.LocalDateTime

interface HistoryResponse {
    val historyTime: LocalDateTime?
    val historyName: String?
    val historyMarket: String?
    val historyAmount: Double?
    val historyPrice: Double?
    val historyAction: String?
    val progress: Int?
}