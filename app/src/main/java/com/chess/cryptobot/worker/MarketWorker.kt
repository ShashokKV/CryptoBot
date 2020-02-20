package com.chess.cryptobot.worker

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chess.cryptobot.content.pairs.AllPairsPreferences
import com.chess.cryptobot.enricher.PairResponseEnricher
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.room.CryptoBotDatabase
import com.chess.cryptobot.model.room.ProfitPair
import com.chess.cryptobot.util.CoinInfo
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer

class MarketWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var allPairNames: Set<String> = AllPairsPreferences(applicationContext).items
    private var coinInfo: CoinInfo? = null

    override fun doWork(): Result {
        cleanDatabase()
        val marketFactory = MarketFactory()
        val context = applicationContext
        val markets = marketFactory.getMarkets(context, PreferenceManager.getDefaultSharedPreferences(context))
        val tickerPairs: List<Pair>
        try {
            coinInfo = CoinInfo(markets)
            tickerPairs = getTickerPairs(markets)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        val profitPairs: MutableList<Pair> = ArrayList()
        try {
            tickerPairs.forEach{ pair: Pair ->
                val tmpPair: Pair = PairResponseEnricher(pair).countPercent().pair
                if (tmpPair.percent > 0) profitPairs.add(tmpPair)
            }
        } catch (e: Exception) {
            Log.d(TAG, e.localizedMessage, e)
            return Result.failure()
        }
        saveToDatabase(profitPairs)
        return Result.success()
    }

    @Throws(MarketException::class)
    private fun getTickerPairs(markets: List<Market?>): List<Pair> {
        val tickerPairs: MutableList<Pair> = ArrayList()
        for (market in markets) {
            val tickers = market!!.getTicker()
            tickers.forEach { ticker: TickerResponse ->
                val tickerName = ticker.marketName
                if (allPairNames.contains(tickerName)) {
                    val pair = createOrGetPair(tickerName, tickerPairs)
                    if (coinInfo!!.checkCoinStatus(pair.baseName) && coinInfo!!.checkCoinStatus(pair.marketName)) {
                        enrichFromTickerByMarket(pair, ticker, market.getMarketName())
                        if (!tickerPairs.contains(pair)) tickerPairs.add(pair)
                    }
                }
            }
        }
        return tickerPairs
    }

    private fun createOrGetPair(tickerName: String, pairs: List<Pair>): Pair {
        val pair = Pair.fromPairName(tickerName)
        return if (pairs.contains(pair)) pairs[pairs.indexOf(pair)] else pair
    }

    private fun enrichFromTickerByMarket(pair: Pair, ticker: TickerResponse, marketName: String) {
        if (marketName == Market.BITTREX_MARKET) {
            pair.bittrexAsk = ticker.tickerAsk!!
            pair.bittrexBid = ticker.tickerBid!!
        } else {
            pair.binanceAsk = ticker.tickerAsk!!
            pair.binanceBid = ticker.tickerBid!!
        }
    }

    private fun cleanDatabase() {
        val database = CryptoBotDatabase.getInstance(applicationContext)
        val dao = database!!.profitPairDao
        val filterDate = LocalDateTime.now().minusDays(31)
        val profitPairs = dao!!.getLowerThanDate(filterDate)
        dao.deleteAll(profitPairs)
    }

    private fun saveToDatabase(pairs: List<Pair>) {
        if (pairs.isEmpty()) return
        val database = CryptoBotDatabase.getInstance(applicationContext)
        val pairDao = database!!.profitPairDao
        val profitPairs: MutableList<ProfitPair?> = ArrayList()
        pairs.forEach(Consumer { pair: Pair ->
            val profitPair = ProfitPair()
            profitPair.dateCreated = LocalDateTime.now()
            profitPair.pairName = pair.name
            profitPair.percent = pair.percent
            profitPairs.add(profitPair)
        })
        pairDao!!.insertAll(profitPairs)
    }

    companion object {
        private val TAG = MarketWorker::class.java.simpleName
    }
}