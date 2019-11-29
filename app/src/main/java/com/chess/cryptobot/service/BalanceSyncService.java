package com.chess.cryptobot.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.exceptions.SyncServiceException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.util.CoinInfo;
import com.chess.cryptobot.view.notification.NotificationBuilder;
import com.chess.cryptobot.view.notification.NotificationID;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.chess.cryptobot.market.Market.BITTREX_MARKET;
import static com.chess.cryptobot.market.Market.LIVECOIN_MARKET;

public class BalanceSyncService extends IntentService {
    private static final String CHANNEL_ID = "balance_sync_channel";
    private String resultInfo = "";
    private CoinInfo coinInfo;
    private final Map<String, Market> marketsMap = new HashMap<>();

    public BalanceSyncService() {
        super("BalanceSyncService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        List<String> coinNames = intent.getStringArrayListExtra("coinNames");

        MarketFactory marketFactory = new MarketFactory();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        List<Market> markets = marketFactory.getMarkets(this, preferences);
        markets.forEach(market -> marketsMap.put(market.getMarketName(), market));

        try {
            coinInfo = new CoinInfo(markets);
        } catch (MarketException e) {
            updateInfo("BalanceSync", "Can't init coinInfo: " + e.getMessage());
            makeNotification();
            return;
        }

        coinNames.forEach(coinName -> {
            try {
                sync(coinName, markets);
            } catch (SyncServiceException e) {
                updateInfo(coinName, e.getMessage());
            }
        });

        makeNotification();
    }

    private void sync(String coinName, List<Market> markets) throws SyncServiceException {
        Double minBalance = new BalancePreferences(this).getMinBalance(coinName);

        if (minBalance == 0.0d) {
            throw new SyncServiceException("Min balance not set");
        }

        if (!coinInfo.checkCoinStatus(coinName)) {
            throw new SyncServiceException("Not active");
        }

        Map<String, Double> marketAmounts;
        try {
            marketAmounts = getMarketsAmounts(markets, coinName);
        } catch (MarketException e) {
            throw new SyncServiceException(e.getMessage());
        }
        if (marketAmounts == null) throw new SyncServiceException("Can't get amounts");

        CoinMover coinMover = new CoinMover(minBalance, coinName);
        coinMover.setAmounts(marketAmounts);
        coinMover.setDirection(BITTREX_MARKET, LIVECOIN_MARKET);

        if (!coinMover.checkAndMove()) {
            coinMover.setDirection(LIVECOIN_MARKET, BITTREX_MARKET);
            coinMover.checkAndMove();
        }
    }

    private Map<String, Double> getMarketsAmounts(List<Market> markets, String coinName) throws MarketException {
        Map<String, Double> marketAmounts = new HashMap<>();
        for (Market market : markets) {
            marketAmounts.put(market.getMarketName(), market.getAmount(coinName));
        }
        return marketAmounts;
    }

    private void updateInfo(String coinName, String message) {
        resultInfo = resultInfo.concat(String.format("%s: %s%s", coinName, message, System.lineSeparator()));
    }

    private void makeNotification() {
        if (resultInfo.isEmpty()) return;
        new NotificationBuilder(this)
                .setNotificationId(NotificationID.getID())
                .setChannelId(CHANNEL_ID)
                .setNotificationText(resultInfo)
                .setChannelName("Balance sync service")
                .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setTitle("Balance sync result")
                .buildAndNotify();

        resultInfo = "";
    }

    class CoinMover {
        private final Double minBalance;
        private final String coinName;
        private Map<String, Double> amounts;
        private String moveFrom;
        private String moveTo;

        CoinMover(Double minBalance, String coinName) {
            this.minBalance = minBalance;
            this.coinName = coinName;
        }

        void setAmounts(Map<String, Double> amounts) {
            this.amounts = amounts;
        }

        private void setDirection(String moveFrom, String moveTo) {
            this.moveFrom = moveFrom;
            this.moveTo = moveTo;
        }

        private boolean checkAndMove() throws SyncServiceException {
            Double fromAmount = getAmount(amounts, moveFrom);
            Double toAmount = getAmount(amounts, moveTo);
            Double fee = coinInfo.getFee(moveFrom, coinName);

            Market moveFromMarket = Objects.requireNonNull(marketsMap.get(moveFrom));
            Market moveToMarket = Objects.requireNonNull(marketsMap.get(moveTo));

            Double delta = getDelta(toAmount, minBalance);
            if (needSync(delta)) {
                delta = formatAmount(recalculateDelta(fromAmount, toAmount, fee));
                checkDelta(delta, fromAmount);
                try {
                    moveBalances(moveFromMarket, moveToMarket, coinName, delta);
                    return true;
                } catch (MarketException e) {
                    throw new SyncServiceException(e.getMessage());
                }
            }
            return false;
        }

        private Double getAmount(Map<String, Double> amounts, String marketName) throws SyncServiceException {
            Double amount = amounts.get(marketName);
            if (amount == null) throw new SyncServiceException("Can't get amount");
            return amount;
        }

        private Double recalculateDelta(Double fromAmount, Double toAmount, Double fee) {
            return (((fromAmount + toAmount) / 2) - toAmount) + fee;
        }

        private Double formatAmount(Double amount) {
            BigDecimal bd = new BigDecimal(amount).setScale(8, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        private Double getDelta(Double amount, Double minBalance) {
            return minBalance - amount;
        }

        private boolean needSync(Double delta) {
            return delta > 0;
        }

        private void checkDelta(Double delta, Double fromAmount) throws SyncServiceException {
            if (fromAmount < delta) throw new SyncServiceException("Not enough coins");
        }

        private void moveBalances(Market moveFrom, Market moveTo, String coinName, Double amount) throws MarketException {
            String address = moveTo.getAddress(coinName);
            moveFrom.sendCoins(coinName, amount, address);

            updateInfo(coinName, String.format("%s sent from %s", amount.toString(), moveFrom.getMarketName()));
        }
    }
}