package com.chess.cryptobot.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.chess.cryptobot.R;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.BittrexMarket;
import com.chess.cryptobot.market.LivecoinMarket;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.view.notification.NotificationBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.chess.cryptobot.service.TradingService.Strategy.MIN_TRADE;

public class TradingService extends IntentService {
    private final static int NOTIFICATION_ID = 400500;
    private static final String CHANNEL_ID = "trading_chanel";
    private String resultInfo = "";
    private Pair pair;
    private BittrexMarket bittrexMarket;
    private LivecoinMarket livecoinMarket;
    private Double livecoinBaseAmount;
    private Double livecoinMarketAmount;
    private Double bittrexBaseAmount;
    private Double bittrexMarketAmount;
    private Double minMarketQuantity;

    public TradingService() {
        super("TradingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;

        initFromIntent(intent);
        initMarkets();

        try {
            initAmounts();
        } catch (MarketException e) {
            makeNotification(e.getMessage());
        }

        Trader trader = new Trader(pair, getStrategy());
        if (trader.quantity <= 0) return;

        try {
            trader.buy();
            trader.sell();
        } catch (MarketException e) {
            trader.updateInfo(e.getMessage());
            makeNotification(resultInfo);
            return;
        }

        makeNotification(resultInfo);

        trader.syncBalance();
    }

    private void initFromIntent(Intent intent) {
        this.pair = (Pair) intent.getSerializableExtra(Pair.class.getName());
        minMarketQuantity = intent.getDoubleExtra("minQuantity", 0.0d);
    }

    private void initMarkets() {
        List<Market> markets = new MarketFactory().getMarkets(this, PreferenceManager.getDefaultSharedPreferences(this));
        markets.forEach(market -> {
            if (market instanceof BittrexMarket) {
                bittrexMarket = (BittrexMarket) market;
            } else if (market instanceof LivecoinMarket) {
                livecoinMarket = (LivecoinMarket) market;
            }
        });
    }

    private void initAmounts() throws MarketException {
        livecoinBaseAmount = livecoinMarket.getAmount(pair.getBaseName());
        bittrexBaseAmount = bittrexMarket.getAmount(pair.getBaseName());
        livecoinMarketAmount = livecoinMarket.getAmount(pair.getMarketName());
        bittrexMarketAmount = bittrexMarket.getAmount(pair.getMarketName());
    }

    private Strategy getStrategy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return Strategy.valueOf(preferences.getString(getResources().getString(R.string.trade_strategy), "MIN_PAIR"));
    }

    private void makeNotification(String message) {
        if (message.isEmpty()) return;
        new NotificationBuilder(this)
                .setNotificationId(NOTIFICATION_ID)
                .setChannelId(CHANNEL_ID)
                .setNotificationText(resultInfo)
                .setChannelName("Trading service")
                .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setTitle("Trading result")
                .setNotificationText(message)
                .buildAndNotify();

        resultInfo = "";
    }

    private class Trader {
        private final Double bidPrice;
        private final Double askPrice;
        private Double quantity;
        private final Market sellMarket;
        private final Market buyMarket;
        private final Double baseAmount;
        private final Double marketAmount;
        private final String sellPairName;
        private final String buyPairName;
        private Strategy strategy;

        Trader(Pair pair, Strategy strategy) {
            this.strategy = strategy;
            if ((pair.getBittrexBid() - pair.getLivecoinAsk()) > (pair.getLivecoinBid() - pair.getBittrexAsk())) {
                bidPrice = pair.getBittrexBid();
                askPrice = pair.getLivecoinAsk();
                sellMarket = bittrexMarket;
                buyMarket = livecoinMarket;
                baseAmount = livecoinBaseAmount / askPrice;
                marketAmount = bittrexMarketAmount;
                quantity = countMinQuantity(pair.getBittrexBidQuantity(), pair.getLivecoinAskQuantity());
                sellPairName = pair.getPairNameForMarket(bittrexMarket.getMarketName());
                buyPairName = pair.getPairNameForMarket(livecoinMarket.getMarketName());
            } else {
                bidPrice = pair.getLivecoinBid();
                askPrice = pair.getBittrexAsk();
                sellMarket = livecoinMarket;
                buyMarket = bittrexMarket;
                baseAmount = bittrexBaseAmount / askPrice;
                marketAmount = livecoinMarketAmount;
                quantity = countMinQuantity(pair.getBittrexBidQuantity(), pair.getLivecoinAskQuantity());
                sellPairName = pair.getPairNameForMarket(livecoinMarket.getMarketName());
                buyPairName = pair.getPairNameForMarket(bittrexMarket.getMarketName());
            }
        }

        private Double countMinQuantity(Double bidQuantity, Double askQuantity) {
            Double minAvailableAmount = baseAmount<marketAmount ? baseAmount : marketAmount;

            switch (strategy) {
                case MIN_PAIR:
                    quantity = bidQuantity<askQuantity ? bidQuantity : askQuantity;
                    break;

                case MAX_PAIR:
                    quantity = bidQuantity>askQuantity ? bidQuantity : askQuantity;
                    break;
                case ALL_BALANCE:
                    quantity = minAvailableAmount;
                    break;
            }

            if (quantity<minMarketQuantity) {
                if (strategy == MIN_TRADE) {
                    quantity = minMarketQuantity;
                }else {
                    return 0.0d;
                }
            }

            if (quantity>minAvailableAmount) return 0.0d;

            //-1% for trading fee
            quantity = formatAmount(quantity - (quantity / 100));

            return quantity;
        }

        private void buy() throws MarketException {
            Double price = formatAmount(askPrice);
            String operationId = buyMarket.buy(buyPairName, price, quantity);
            updateInfo(String.format(Locale.getDefault(), "buy %.8f%s for %.8f on %s, id=%s", quantity, buyPairName,
                    price, buyMarket.getMarketName(), operationId));
        }

        private void sell() throws MarketException {
            Double price = formatAmount(bidPrice);
            String operationId = sellMarket.sell(sellPairName, price, quantity);
            updateInfo(String.format(Locale.getDefault(), "sell %.8f%s for %.8f on %s, id=%s", quantity, sellPairName,
                    price, sellMarket.getMarketName(), operationId));
        }

        private Double formatAmount(Double amount) {
            BigDecimal bd = new BigDecimal(amount).setScale(8, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        private void updateInfo(String text) {
            resultInfo = resultInfo.concat(text).concat(System.lineSeparator());
        }

        private void syncBalance() {
            ArrayList<String> coinNames = new ArrayList<>();
            coinNames.add(pair.getBaseName());
            coinNames.add(pair.getMarketName());
            Intent intent = new Intent(TradingService.this, BalanceSyncService.class);
            intent.putExtra("coinNames", coinNames);
            TradingService.this.startService(intent);
        }
    }

    public enum Strategy {
        MIN_PAIR,
        MAX_PAIR,
        MIN_TRADE,
        ALL_BALANCE
    }
}
