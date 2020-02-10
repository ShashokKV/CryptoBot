package com.chess.cryptobot.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.BittrexMarket;
import com.chess.cryptobot.market.LivecoinMarket;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.view.notification.NotificationBuilder;
import com.chess.cryptobot.view.notification.NotificationID;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TradingService extends IntentService {
    public static String workingOnPair;
    private static final String CHANNEL_ID = "trading_chanel";
    private static final Double minBtcAmount = 0.0005;
    private static final Double minEthAmount = 0.025;
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
        if (isApiKeysEmpty()) {
            makeNotification("Api keys are empty", "Please fill api keys in settings or turn AutoTrade off");
            return;
        }

        try {
            initAmounts();
        } catch (MarketException e) {
            makeNotification("Init amounts exception", e.getMessage());
            return;
        }

         Trader trader = new Trader(pair);
        if (trader.quantity <= minMarketQuantity) return;

        try {
            trader.buy();
            trader.sell();
        } catch (MarketException e) {
            trader.updateInfo(String.format(Locale.US, "%.8f%s bid %.8f; ask %.8f; error: %s",
                    trader.quantity,
                    trader.buyPairName,
                    trader.bidPrice,
                    trader.askPrice,
                    e.getMessage()));
            makeNotification("Trade exception", resultInfo);
            return;
        }

        makeNotification("Trading results", resultInfo);

        trader.syncBalance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        workingOnPair = null;
    }

    private void initFromIntent(Intent intent) {
        this.pair = (Pair) intent.getSerializableExtra(Pair.class.getName());
        workingOnPair = pair.getName();
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

    private boolean isApiKeysEmpty() {
        return (bittrexMarket.keysIsEmpty() || livecoinMarket.keysIsEmpty());
    }

    private void initAmounts() throws MarketException {
        livecoinBaseAmount = livecoinMarket.getAmount(pair.getBaseName());
        bittrexBaseAmount = bittrexMarket.getAmount(pair.getBaseName());
        livecoinMarketAmount = livecoinMarket.getAmount(pair.getMarketName());
        bittrexMarketAmount = bittrexMarket.getAmount(pair.getMarketName());
    }

    private void makeNotification(String title, String message) {
        if (message.isEmpty()) return;
        new NotificationBuilder(this)
                .setNotificationId(NotificationID.getID())
                .setChannelId(CHANNEL_ID)
                .setNotificationText(resultInfo)
                .setChannelName("Trading service")
                .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setTitle(title)
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

        Trader(Pair pair) {
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
                quantity = countMinQuantity(pair.getLivecoinBidQuantity(), pair.getBittrexAskQuantity());
                sellPairName = pair.getPairNameForMarket(livecoinMarket.getMarketName());
                buyPairName = pair.getPairNameForMarket(bittrexMarket.getMarketName());
            }
        }

        private Double countMinQuantity(Double bidQuantity, Double askQuantity) {
            Double minAvailableAmount = baseAmount<marketAmount ? baseAmount : marketAmount;
            quantity = bidQuantity<askQuantity ? bidQuantity : askQuantity;

            if (quantity>minAvailableAmount) quantity = minAvailableAmount;

            //-1% for trading fee
            quantity = formatAmount(quantity - (quantity / 100));

            if (pair.getBaseName().equals("BTC")) {
                if ((quantity * askPrice) < minBtcAmount) quantity = 0.0d;
            } else if (pair.getBaseName().equals("ETH")) {
                if ((quantity * askPrice) < minEthAmount) quantity = 0.0d;
            }

            return quantity;
        }

        private void buy() throws MarketException {
            Double price = formatAmount(askPrice);
            buyMarket.buy(buyPairName, price, quantity);
            updateInfo(String.format(Locale.getDefault(), "buy %.8f%s for %.8f on %s", quantity, buyPairName,
                    price, buyMarket.getMarketName()));
        }

        private void sell() throws MarketException {
            Double price = formatAmount(bidPrice);
            sellMarket.sell(sellPairName, price, quantity);
            updateInfo(String.format(Locale.getDefault(), "sell %.8f%s for %.8f on %s", quantity, sellPairName,
                    price, sellMarket.getMarketName()));
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
}
