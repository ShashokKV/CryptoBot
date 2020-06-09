package com.chess.cryptobot.worker

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.room.BtcBalance
import com.chess.cryptobot.model.room.CryptoBotDatabase
import java.time.LocalDateTime

class BalanceWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var allCoinNames: Set<String>? = null
    private fun init() {
        allCoinNames = BalancePreferences(applicationContext).items
    }

    override fun doWork(): Result {
        cleanDatabase()
        val marketFactory = MarketFactory()
        val context = applicationContext
        var btcSum = 0.0
        val markets = marketFactory.getMarkets(context, PreferenceManager.getDefaultSharedPreferences(context))
        if (isApiKeysEmpty(markets)) return Result.success()
        for (market in markets) {
            val amount = try {
                market!!.getAmount("BTC")
            } catch (e: MarketException) {
                Log.d(TAG, e.localizedMessage, e)
                return Result.failure()
            }
            btcSum += amount
        }
        if (btcSum > 0) saveToDatabase(btcSum)
        return Result.success()
    }

    private fun isApiKeysEmpty(markets: List<Market?>): Boolean {
        for (market in markets) {
            if (market!!.keysIsEmpty()) return true
        }
        return false
    }

    private fun cleanDatabase() {
        val database = CryptoBotDatabase.getInstance(applicationContext)
        val dao = database!!.btcBalanceDao
        val filterDate = LocalDateTime.now().minusDays(31)
        val balances = dao!!.getLowerThanDate(filterDate)
        dao.deleteAll(balances)
    }

    private fun saveToDatabase(btcSum: Double) {
        val database = CryptoBotDatabase.getInstance(applicationContext)
        val dao = database?.btcBalanceDao
        val balance = BtcBalance()
        balance.balance = btcSum.toFloat()
        balance.dateCreated = LocalDateTime.now()
        dao?.insert(balance)
    }

    companion object {
        private const val TAG = "BalanceWorker"
    }

    init {
        init()
    }
}