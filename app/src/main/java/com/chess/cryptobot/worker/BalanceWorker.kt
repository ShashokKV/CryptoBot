package com.chess.cryptobot.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketClient
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.room.CryptoBalance
import com.chess.cryptobot.model.room.CryptoBotDatabase
import java.time.LocalDateTime

class BalanceWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var allCoinNames: Set<String>? = null
    private fun init() {
        allCoinNames = BalancePreferences(applicationContext).items
    }

    override fun doWork(): Result {
        cleanDatabase()
        var btcSum = 0.0
        val usdSum: Double
        val markets = MarketFactory.getInstance(applicationContext).getMarkets()
        markets.forEach(Market::resetBalance)
        if (isApiKeysEmpty(markets)) return Result.success()

        val tickerMap = try {
            getTickers(markets)
        } catch (e: MarketException) {
            Log.e(TAG, Log.getStackTraceString(e))
            return Result.failure()
        }

        allCoinNames?.forEach { coinName ->
            var amount = 0.0
            for (market in markets) {
                val marketAmount = try {
                    market.getAmount(coinName)
                } catch (e: MarketException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                    return Result.failure()
                }
                if (marketAmount > 0) {
                    if (coinName == "BTC") {
                        btcSum += marketAmount
                    } else {
                        val pairName: String = if (coinName == "USDT") {
                            "USDT/BTC"
                        } else {
                            "BTC/$coinName"
                        }
                        val ticker = tickerMap[market.getMarketName()]
                                ?.first { tickerResponse -> tickerResponse.tickerName == pairName }
                        btcSum += if (coinName == "USDT") {
                            marketAmount / (ticker?.tickerBid ?: 1.0)
                        } else {
                            marketAmount * (ticker?.tickerBid ?: 0.0)
                        }
                    }
                    amount += marketAmount
                }
            }
            if (amount > 0) saveToDatabase(amount, coinName)
        }
        if (btcSum > 0) {
            saveToDatabase(btcSum, "BTC total")
            var btcPrice = 0.0
            markets.forEach { market ->
                val pairName = "USDT/BTC"
                val ticker = tickerMap[market.getMarketName()]
                        ?.first { tickerResponse -> tickerResponse.tickerName == pairName }
                btcPrice += ticker?.tickerBid?:0.0
            }
            btcPrice /= markets.size
            usdSum = btcSum*btcPrice
            saveToDatabase(usdSum, "USD total")
        }

        return Result.success()
    }

    private fun getTickers(markets: List<MarketClient?>): Map<String, List<TickerResponse>?> {
        val tickersMap = HashMap<String, List<TickerResponse>?>()
        markets.forEach { market ->
            val ticker = market?.getTicker()
            tickersMap[market!!.getMarketName()] = ticker
        }
        return tickersMap
    }

    private fun isApiKeysEmpty(markets: List<Market?>): Boolean {
        for (market in markets) {
            if (market!!.keysIsEmpty()) return true
        }
        return false
    }

    private fun cleanDatabase() {
        val database = CryptoBotDatabase.getInstance(applicationContext)
        val dao = database.cryptoBalanceDao
        val filterDate = LocalDateTime.now().minusDays(31)
        val balances = dao.getLowerThanDate(filterDate)
        dao.deleteAll(balances)
    }

    private fun saveToDatabase(amount: Double, coinName: String) {
        val database = CryptoBotDatabase.getInstance(applicationContext)
        val dao = database.cryptoBalanceDao
        val balance = CryptoBalance(name = coinName, balance = amount.toFloat(), dateCreated = LocalDateTime.now())
        dao.insert(balance)
    }

    companion object {
        private const val TAG = "BalanceWorker"
    }

    init {
        init()
    }
}