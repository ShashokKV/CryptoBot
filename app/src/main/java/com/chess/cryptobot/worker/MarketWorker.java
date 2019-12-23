package com.chess.cryptobot.worker;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.chess.cryptobot.content.pairs.AllPairsPreferences;
import com.chess.cryptobot.enricher.PairResponseEnricher;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.TickerResponse;
import com.chess.cryptobot.model.room.CryptoBotDatabase;
import com.chess.cryptobot.model.room.ProfitPair;
import com.chess.cryptobot.model.room.ProfitPairDao;
import com.chess.cryptobot.util.CoinInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MarketWorker extends Worker {
    private Set<String> allPairNames;
    private CoinInfo coinInfo;
    private static final String TAG = MarketWorker.class.getSimpleName();

    public MarketWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        init();
    }

    private void init() {
        allPairNames = new AllPairsPreferences(getApplicationContext()).getItems();
    }

    @NonNull
    @Override
    public Result doWork() {
        cleanDatabase();

        MarketFactory marketFactory = new MarketFactory();
        Context context = getApplicationContext();

        List<Market> markets = marketFactory.getMarkets(context, PreferenceManager.getDefaultSharedPreferences(context));
        List<Pair> tickerPairs;
        try {
            coinInfo = new CoinInfo(markets);
            tickerPairs = getTickerPairs(markets);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }

        List<Pair> profitPairs = new ArrayList<>();
        try {
            tickerPairs.forEach(pair -> {
                pair = new PairResponseEnricher(pair).countPercent().getPair();
                if (pair.getPercent() > 0) profitPairs.add(pair);
            });
        }catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage(), e);
            return Result.failure();
        }

        saveToDatabase(profitPairs);
        return Result.success();
    }

    private List<Pair> getTickerPairs(List<Market> markets) throws MarketException {
        List<Pair> tickerPairs = new ArrayList<>();
        for (Market market : markets) {
            List<? extends TickerResponse> tickers = market.getTicker();
            tickers.forEach(ticker -> {
                String tickerName = ticker.getMarketName();
                if (allPairNames.contains(tickerName)) {
                    Pair pair = createOrGetPair(tickerName, tickerPairs);
                    if (coinInfo.checkCoinStatus(pair.getBaseName()) && coinInfo.checkCoinStatus(pair.getMarketName())) {
                        enrichFromTickerByMarket(pair, ticker, market.getMarketName());
                        if (!tickerPairs.contains(pair)) tickerPairs.add(pair);
                    }
                }
            });
        }
        return tickerPairs;
    }

    private Pair createOrGetPair(String tickerName, List<Pair> pairs) {
        Pair pair = Pair.fromPairName(tickerName);
        if (pairs.contains(pair)) return pairs.get(pairs.indexOf(pair));
        return pair;
    }

    private void enrichFromTickerByMarket(Pair pair, TickerResponse ticker, String marketName) {
        if (marketName.equals(Market.BITTREX_MARKET)) {
            pair.setBittrexAsk(ticker.getTickerAsk());
            pair.setBittrexBid(ticker.getTickerBid());
        } else {
            pair.setLivecoinAsk(ticker.getTickerAsk());
            pair.setLivecoinBid(ticker.getTickerBid());
        }
    }

    private void cleanDatabase() {
        CryptoBotDatabase database = CryptoBotDatabase.getInstance(getApplicationContext());
        ProfitPairDao dao = database.getProfitPairDao();

        LocalDateTime filterDate = LocalDateTime.now().minusDays(31);
        List<ProfitPair> profitPairs = dao.getLowerThanDate(filterDate);
        dao.deleteAll(profitPairs);
    }

    private void saveToDatabase(List<Pair> pairs) {
        if (pairs.isEmpty()) return;
        CryptoBotDatabase database = CryptoBotDatabase.getInstance(getApplicationContext());
        ProfitPairDao pairDao = database.getProfitPairDao();

        List<ProfitPair> profitPairs = new ArrayList<>();

        pairs.forEach(pair -> {
            ProfitPair profitPair = new ProfitPair();
            profitPair.setDateCreated(LocalDateTime.now());
            profitPair.setPairName(pair.getName());
            profitPair.setPercent(pair.getPercent());
            profitPairs.add(profitPair);
        });

        pairDao.insertAll(profitPairs);
    }
}
