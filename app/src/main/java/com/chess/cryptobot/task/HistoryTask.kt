package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.history.HistoryHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.History
import java.util.function.Consumer

class HistoryTask(private val holder: ContextHolder, private val state: HistoryHolder.State) : MarketTask<Int, String?>(holder) {
    private var historyList: MutableList<History> = ArrayList()

    override fun preMarketProcess(param: Int) {
    }

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Int): String? {
        if (market.keysIsEmpty()) return ""
        if (state === HistoryHolder.State.HISTORY) {
            historyList.addAll(market.getHistory(holder.context))
        } else {
            historyList.addAll(market.getOpenOrders())
        }
        return ""
    }

    override fun postMarketProcess(result: String?): String {
        historyList.sort()
        return ""
    }

    override fun exceptionProcess(param: Int, exceptionMessage: String?): String? {
        return exceptionMessage!!
    }

    override fun doInPostExecute(result: String?, holder: ContextHolder) {
        val historyHolder = holder as HistoryHolder
        if (result?.isNotEmpty() == true) {
            historyHolder.makeToast(result)
            return
        }
        historyList.forEach(Consumer { viewItem: History? -> historyHolder.add(viewItem!!) })
    }

    override fun doInOnCanceled(result: String?, holder: ContextHolder?) {}

}