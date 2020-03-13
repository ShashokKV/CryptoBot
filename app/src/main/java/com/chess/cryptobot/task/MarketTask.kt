package com.chess.cryptobot.task

import android.os.AsyncTask
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import java.lang.ref.WeakReference

abstract class MarketTask<S, T>(holder: ContextHolder) : AsyncTask<S, Int?, T?>() {
    private val holderWeakReference: WeakReference<ContextHolder> = WeakReference(holder)

    @SafeVarargs
    override fun doInBackground(vararg params: S): T? {
        val param = params[0]
        var result: T? = null
        val holder = holder
        publishProgress()
        preMarketProcess(param)
        if (holder == null) return null
        val markets = holder.markets
        for (market in markets) {
            result = try {
                if (market == null) {
                    cancel(true)
                    return null
                }
                marketProcess(market, param)
            } catch (e: MarketException) {
                cancel(true)
                return exceptionProcess(param, e.message)
            }
        }
        return postMarketProcess(result)
    }

    protected abstract fun preMarketProcess(param: S)

    @Throws(MarketException::class)
    protected abstract fun marketProcess(market: Market, param: S): T

    protected abstract fun postMarketProcess(result: T?): T

    protected abstract fun exceptionProcess(param: S, exceptionMessage: String?): T

    override fun onPostExecute(result: T?) {
        val holder = holder ?: return
        holder.hideSpinner()
        if (result == null) return
        doInPostExecute(result, holder)
    }

    protected abstract fun doInPostExecute(result: T, holder: ContextHolder)

    override fun onCancelled(result: T?) {
        val holder = holder
        holder?.hideSpinner()
        result?.let { doInOnCanceled(it, holder) }
    }

    protected abstract fun doInOnCanceled(result: T, holder: ContextHolder?)
    override fun onProgressUpdate(vararg values: Int?) {
        val holder = holder
        holder?.showSpinner()
    }

    private val holder: ContextHolder?
        get() {
            val holder = holderWeakReference.get()
            if (holder == null) {
                cancel(true)
                return null
            }
            return holder
        }

}