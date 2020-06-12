package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.history.HistoryHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.History

class HistoryTask(private val holder: ContextHolder, private val state: HistoryHolder.State) : MarketTask<Int, String?>(holder) {
    private var historyList: MutableList<History> = ArrayList()

    override fun preMarketProcess(param: Int) {
    }

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Int): String? {
        if (market.keysIsEmpty()) return ""
        val marketHistory: List<History> = if (state === HistoryHolder.State.HISTORY) {
            market.getHistory(holder.context)
        } else {
            market.getOpenOrders()
        }
        synchronized(this) {historyList.addAll(marketHistory)}
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
        historyList.forEach { viewItem: History? -> historyHolder.add(viewItem!!) }
    }

    override fun doInOnCanceled(result: String?, holder: ContextHolder?) {}

}