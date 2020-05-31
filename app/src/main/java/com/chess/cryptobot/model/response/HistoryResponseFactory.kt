package com.chess.cryptobot.model.response

import com.chess.cryptobot.model.History
import java.time.ZoneId
import java.util.*

class HistoryResponseFactory(private val historyResponseList: List<HistoryResponse>?) {
    val history: List<History>
        get() {
            val historyList: MutableList<History> = ArrayList()
            if (historyResponseList==null) return historyList
            for (historyResponse in historyResponseList) {

                val history =  History()
                history.action = historyResponse.historyAction
                history.amount = historyResponse.historyAmount
                history.currencyName = historyResponse.historyName
                history.dateTime = historyResponse.historyTime?.withZoneSameInstant(ZoneId.systemDefault())?.toLocalDateTime()
                history.market = historyResponse.historyMarket
                history.price = historyResponse.historyPrice
                history.progress = historyResponse.progress
                historyList.add(history)
            }
            return historyList
        }

}