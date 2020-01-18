package com.chess.cryptobot.worker;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.room.BtcBalance;
import com.chess.cryptobot.model.room.BtcBalanceDao;
import com.chess.cryptobot.model.room.CryptoBotDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class BalanceWorker extends Worker {
    private static final String TAG = "BalanceWorker";
    private Set<String> allCoinNames;

    public BalanceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        init();
    }

    private void init() {
        allCoinNames = new BalancePreferences(getApplicationContext()).getItems();
    }

    @NonNull
    @Override
    public Result doWork() {
        cleanDatabase();

        MarketFactory marketFactory = new MarketFactory();
        Context context = getApplicationContext();

        double btcSum = 0.0d;
        List<Market> markets = marketFactory.getMarkets(context, PreferenceManager.getDefaultSharedPreferences(context));
        for (String coinName: allCoinNames) {
            for (Market market: markets) {
                Double amount;
                try {
                    amount = market.getAmount(coinName);
                } catch (MarketException e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                    return Result.failure();
                }
                if (amount>0) {
                    if (coinName.equals("BTC")) {
                        btcSum+=amount;
                    }else {
                        Double price;
                        try {
                            OrderBookResponse orderBook = market.getOrderBook(new Pair("BTC", coinName).getPairNameForMarket(market.getMarketName()));
                            price = orderBook.bids().get(0).getValue();
                        } catch (MarketException e) {
                            Log.d(TAG, e.getLocalizedMessage(), e);
                            return Result.failure();
                        }
                        if (price>0) {
                            btcSum+=amount*price;
                        }
                    }
                }
            }
        }

        if (btcSum>0) saveToDatabase(btcSum);

        return Result.success();
    }

    private void cleanDatabase() {
        CryptoBotDatabase database = CryptoBotDatabase.getInstance(getApplicationContext());
        BtcBalanceDao dao = database.getBtcBalanceDao();

        LocalDateTime filterDate = LocalDateTime.now().minusDays(31);
        List<BtcBalance> balances = dao.getLowerThanDate(filterDate);
        dao.deleteAll(balances);
    }

    private void saveToDatabase(Double btcSum) {
        CryptoBotDatabase database = CryptoBotDatabase.getInstance(getApplicationContext());
        BtcBalanceDao dao = database.getBtcBalanceDao();

        BtcBalance balance = new BtcBalance();
        balance.setBalance(btcSum.floatValue());
        balance.setDateCreated(LocalDateTime.now());

        dao.insert(balance);
    }

}
