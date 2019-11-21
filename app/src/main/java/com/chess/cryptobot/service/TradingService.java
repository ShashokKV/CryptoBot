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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TradingService extends IntentService {
    private final static int NOTIFICATION_ID = 400500;
    private static final String CHANNEL_ID = "trading_chanel";
    private String resultInfo = "";
    private Pair pair;
    private BittrexMarket bittrexMarket;
    private LivecoinMarket livecoinMarket;
    private Double livecoinBaseFee;
    private Double livecoinMarketFee;
    private Double bittrexBaseFee;
    private Double bittrexMarketFee;
    private Double livecoinBaseAmount;
    private Double livecoinMarketAmount;
    private Double bittrexBaseAmount;
    private Double bittrexMarketAmount;


    public TradingService() {
        super("TradingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent==null) return;
        initFromIntent(intent);
        initMarkets();
        try {
            initAmounts();
        }catch (MarketException e) {
            makeNotification(e.getMessage());
        }

        Trader trader = new Trader(pair);
        if(trader.countMinQuantity()<=0) return;

        try {
            trader.buy();
            trader.sell();
        }catch (MarketException e) {
            trader.updateInfo(e.getMessage());
        }

        makeNotification(resultInfo);

          trader.syncBalance();
    }

    private void initFromIntent(Intent intent) {
        this.pair = (Pair) intent.getSerializableExtra(Pair.class.getName());
        livecoinBaseFee = intent.getDoubleExtra("livecoinBaseFee", 0.0d);
        livecoinMarketFee = intent.getDoubleExtra("livecoinMarketFee", 0.0d);
        bittrexBaseFee = intent.getDoubleExtra("bittrexBaseFee", 0.0d);
        bittrexMarketFee = intent.getDoubleExtra("bittrexMarketFee", 0.0d);
    }

    private void initMarkets() {
        List<Market> markets = new MarketFactory().getMarkets(this, PreferenceManager.getDefaultSharedPreferences(this));
        markets.forEach(market -> {
            if (market instanceof BittrexMarket) {
                bittrexMarket = (BittrexMarket) market;
            }else if (market instanceof LivecoinMarket) {
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

        resultInfo="";
    }

    private class Trader {
        private Double bidPrice, askPrice;
        private Double quantity;
        private Market sellMarket, buyMarket;
        private Double sellFee, buyFee;
        private Double baseAmount, marketAmount;
        private String sellPairName, buyPairName;

        Trader(Pair pair) {
            if ((pair.getBittrexBid()-pair.getLivecoinAsk())>(pair.getLivecoinBid()-pair.getBittrexAsk())) {
                bidPrice = pair.getBittrexBid();
                askPrice = pair.getLivecoinAsk();
                sellMarket = bittrexMarket;
                buyMarket = livecoinMarket;
                if (pair.getBittrexBidQuantity()<pair.getLivecoinAskQuantity()) {
                    quantity = pair.getBittrexBidQuantity();
                }else {
                    quantity = pair.getLivecoinAskQuantity();
                }
                buyFee = livecoinMarketFee;
                sellFee = bittrexBaseFee;
                baseAmount = livecoinBaseAmount;
                marketAmount = bittrexMarketAmount;
                sellPairName = pair.getPairNameForMarket(bittrexMarket.getMarketName());
                buyPairName = pair.getPairNameForMarket(livecoinMarket.getMarketName());
            } else {
                bidPrice = pair.getLivecoinBid();
                askPrice = pair.getBittrexAsk();
                sellMarket = livecoinMarket;
                buyMarket = bittrexMarket;
                if (pair.getLivecoinBidQuantity()<pair.getBittrexAskQuantity()) {
                    quantity = pair.getLivecoinBidQuantity();
                }else {
                    quantity = pair.getBittrexAskQuantity();
                }
                buyFee = bittrexMarketFee;
                sellFee = livecoinBaseFee;
                baseAmount = bittrexBaseAmount;
                marketAmount = livecoinMarketAmount;
                sellPairName = pair.getPairNameForMarket(livecoinMarket.getMarketName());
                buyPairName = pair.getPairNameForMarket(bittrexMarket.getMarketName());
            }
        }

        private Double countMinQuantity() {
            Double buyQuantity, sellQuantity, resultQuantity;
            buyQuantity = (baseAmount/askPrice)-buyFee;
            sellQuantity = marketAmount-sellFee;
            if (buyQuantity<sellQuantity) {
                resultQuantity = buyQuantity;
            } else {
                resultQuantity = sellQuantity;
            }
            if (resultQuantity<quantity) {
                quantity = resultQuantity;
            }
            //-1% for trading fee
            quantity = formatAmount(quantity - (quantity/100));

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




}
