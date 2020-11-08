package com.chess.cryptobot.worker

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.content.pairs.AllPairsPreferences
import com.chess.cryptobot.enricher.PairResponseEnricher
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.market.MarketClient
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.CurrenciesResponse
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.TradeLimitResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse
import com.chess.cryptobot.model.room.CoinInfo
import com.chess.cryptobot.model.room.CryptoBotDatabase
import com.chess.cryptobot.model.room.PairMinTradeSize
import com.chess.cryptobot.model.room.ProfitPair
import com.chess.cryptobot.util.MarketInfoReader
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class MarketWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var allPairNames: HashSet<String>? = null
    private lateinit var markets: List<MarketClient?>
    private var database: CryptoBotDatabase? = null
    private lateinit var marketInfoReader: MarketInfoReader

    override fun doWork(): Result {
        allPairNames = AllPairsPreferences(context).items
        markets = MarketFactory().getMarkets(context, PreferenceManager.getDefaultSharedPreferences(context))
        database = CryptoBotDatabase.getInstance(context)
        marketInfoReader = MarketInfoReader(database)

        try {
            cleanDatabase()
            infoOnCoins()
            infoOnProfitPairs()
            infoOnMinTradeSize()
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage, e)
            return Result.failure()
        }
        return Result.success()
    }

    private fun infoOnProfitPairs() {
        val pairs: MutableList<Pair> = getTickerPairs(markets)
        saveProfitPairsToDatabase(pairs.stream()
                .peek{ pair -> PairResponseEnricher(pair).enrichWithMinPercent(null)}
                .filter{pair -> pair.percent>0}
                .collect(Collectors.toList()))
    }

    @Throws(MarketException::class)
    private fun getTickerPairs(markets: List<Market?>): MutableList<Pair> {
        val tickerPairs: MutableList<Pair> = ArrayList()
        for (market in markets) {
            val tickers = market!!.getTicker()
            tickers.forEach { ticker: TickerResponse ->
                val tickerName = ticker.tickerName
                if (allPairNames?.contains(tickerName)!!) {
                    val pair = createOrGetPair(tickerName, tickerPairs)
                    if (marketInfoReader.checkCoinStatus(pair.baseName) && marketInfoReader.checkCoinStatus(pair.marketName)) {
                        PairResponseEnricher(pair).enrichFromTicker(ticker, market.getMarketName())
                        if (!tickerPairs.contains(pair)) tickerPairs.add(pair)
                    }
                }
            }
        }
        return tickerPairs
    }

    private fun infoOnMinTradeSize() {
        val pairs: MutableList<String> = initPairsNamesFromPrefs()
        val tradeLimits: MutableMap<String, TradeLimitResponse?> = getTradeLimits()
        updatePairsWithTradeLimits(pairs, tradeLimits)
    }

    private fun initPairsNamesFromPrefs(): MutableList<String> {
        val coinNames = BalancePreferences(context).items
        val allPairNames = AllPairsPreferences(context).items
        val pairs = ArrayList<String>()
        if (coinNames != null) {
            for (baseName in coinNames) {
                for (marketName in coinNames) {
                    if (baseName != marketName) {
                        val pairName = "$baseName/$marketName"
                        if (allPairNames != null) {
                            if (allPairNames.contains(pairName)) {
                                pairs.add(pairName)
                            }
                        }
                    }
                }
            }
        }
        return pairs
    }

    private fun getTradeLimits(): MutableMap<String, TradeLimitResponse?> {
        var exception: MarketException? = null
        val tradeLimits: MutableMap<String, TradeLimitResponse?> = ConcurrentHashMap(3)
        markets.parallelStream().forEach {
            it ?: return@forEach
            var minQuantity: TradeLimitResponse? = null
            try {
                minQuantity = it.getMinQuantity()
            } catch (e: MarketException) {
                exception = e
            }
            if (minQuantity != null) tradeLimits[it.getMarketName()] = minQuantity
        }
        if (exception != null) throw exception as MarketException

        return tradeLimits
    }

    private fun updatePairsWithTradeLimits(pairNames: MutableList<String>, tradeLimits: MutableMap<String, TradeLimitResponse?>) {
        val minTradeSizeDao = database?.minTradeSizeDao
        pairNames.forEach { pairName ->
            var resultQuantity = 0.0
            var insert = false
            var pairMinTradeSize = minTradeSizeDao?.getByPairName(pairName)
            if (pairMinTradeSize == null) {
                pairMinTradeSize = PairMinTradeSize()
                pairMinTradeSize.pairName = pairName
                insert = true
            }
            markets.forEach { market ->
                val marketName = market!!.getMarketName()
                val response = tradeLimits[marketName]
                if (response != null) {
                    val pair = Pair.fromPairName(pairName)
                    val quantity = response.getTradeLimitByName(pair.getPairNameForMarket(marketName))
                    if (quantity > resultQuantity) {
                        resultQuantity = quantity
                    }
                    if (marketName == BINANCE_MARKET) {
                        val binanceResponse = response as BinanceResponse
                        pairMinTradeSize.stepSize = binanceResponse.getStepSizeByName(pair.getPairNameForMarket(BINANCE_MARKET))
                                ?: 1.00000000
                    }
                }
            }
            pairMinTradeSize.minTradeSize = resultQuantity
            if (insert) {
                minTradeSizeDao?.insert(pairMinTradeSize)
            } else {
                minTradeSizeDao?.update(pairMinTradeSize)
            }
        }
    }

    private fun infoOnCoins() {
        val coinInfoDao = database?.coinInfoDao
        markets.parallelStream().forEach {
            it ?: return@forEach
            val currencies = it.getCurrencies()
            synchronized(this) {
                for (currency in currencies) {
                    var coinInfo = coinInfoDao?.getByNameAndMarketName(currency.currencyName, it.getMarketName())
                    if (coinInfo == null) {
                        coinInfo = CoinInfo()
                        coinInfo.name = currency.currencyName
                        coinInfo.marketName = it.getMarketName()
                        updateCoinInfo(coinInfo, currency)
                        coinInfoDao?.insert(coinInfo)
                    } else {
                        updateCoinInfo(coinInfo, currency)
                        coinInfoDao?.update(coinInfo)
                    }
                }
            }
        }
    }

    private fun updateCoinInfo(coinInfo: CoinInfo, currency: CurrenciesResponse) {
        coinInfo.status = currency.isActive ?: true
        coinInfo.fee = currency.fee ?: 0.0
    }


    private fun createOrGetPair(tickerName: String, pairs: List<Pair>): Pair {
        val pair = Pair.fromPairName(tickerName)
        return if (pairs.contains(pair)) pairs[pairs.indexOf(pair)] else pair
    }

    private fun cleanDatabase() {
        val dao = database!!.profitPairDao
        val filterDate = LocalDateTime.now().minusDays(31)
        val profitPairs = dao!!.getLowerThanDate(filterDate)
        dao.deleteAll(profitPairs)
    }

    private fun saveProfitPairsToDatabase(pairs: List<Pair>) {
        if (pairs.isEmpty()) return
        val pairDao = database!!.profitPairDao
        val profitPairs: MutableList<ProfitPair?> = ArrayList()
        pairs.forEach { pair: Pair ->
            val profitPair = ProfitPair()
            profitPair.dateCreated = LocalDateTime.now()
            profitPair.pairName = pair.name
            profitPair.percent = pair.percent
            profitPairs.add(profitPair)
        }
        pairDao!!.insertAll(profitPairs)
    }

    companion object {
        private val TAG = MarketWorker::class.java.simpleName
    }
}