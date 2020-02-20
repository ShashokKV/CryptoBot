package com.chess.cryptobot.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class History : ViewItem, Comparable<History?> {
    var dateTime: LocalDateTime? = null
    var market: String? = null
    var currencyName: String? = null
    var action: String? = null
    var amount: Double? = null
    var price: Double? = null
    var progress: Int? = null

    override val name: String
        get() = dateTime!!.format(DateTimeFormatter.ISO_LOCAL_TIME) + market + currencyName

    override operator fun compareTo(other: History?): Int {
        return other?.dateTime!!.compareTo(dateTime)
    }
}