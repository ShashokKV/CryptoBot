package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class MarketTask<S, T>(private val holder: ContextHolder) {
    private val tag = MarketTask::class.qualifiedName

    @SafeVarargs
    fun doInBackground(vararg params: S) {
        val param = params[0]
        var result: T? = null
        val holder = holder

        onProgressUpdate()
        preMarketProcess(param)

        val markets = MarketFactory.getInstance(holder.context).getMarkets()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                markets.parallelStream().forEach { result = marketsLoop(it, param) }
                val postMarketProcessResult = postMarketProcess(result)
                withContext(Dispatchers.Main) {
                    onPostExecute(postMarketProcessResult)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onCancelled(exceptionProcess(param, e.message))
                }
            }
        }
    }

    @Throws(MarketException::class)
    private fun marketsLoop(market: Market, param: S): T? {
        market.resetBalance()
        return marketProcess(market, param)
    }

    protected abstract fun preMarketProcess(param: S)

    @Throws(MarketException::class)
    protected abstract fun marketProcess(market: Market, param: S): T

    protected abstract fun postMarketProcess(result: T?): T

    protected abstract fun exceptionProcess(param: S, exceptionMessage: String?): T

    private fun onPostExecute(result: T?) {
        holder.hideSpinner()
        if (result == null) return
        doInPostExecute(result, holder)
    }

    protected abstract fun doInPostExecute(result: T, holder: ContextHolder)

    private fun onCancelled(result: T?) {
        holder.hideSpinner()
        result?.let { doInOnCanceled(it, holder) }
    }

    protected abstract fun doInOnCanceled(result: T, holder: ContextHolder?)

    private fun onProgressUpdate() {
        val holder = holder
        holder.showSpinner()
    }
}