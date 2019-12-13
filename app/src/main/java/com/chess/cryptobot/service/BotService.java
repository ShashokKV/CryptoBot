package com.chess.cryptobot.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.content.pairs.AllPairsPreferences;
import com.chess.cryptobot.enricher.PairResponseEnricher;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.TradeLimitResponse;
import com.chess.cryptobot.util.CoinInfo;
import com.chess.cryptobot.view.notification.NotificationBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class BotService extends Service {
    private static final int NOTIFICATION_ID = 100500;
    private static final int FOREGROUND_NOTIFICATION_ID = 200500;

    public static boolean isRunning;
    private final static String CHANNEL_ID = "profit_pairs_channel_id";
    private final static String FOREGROUND_CHANNEL_ID = "bot_channel_id";
    private Timer botTimer;
    private Integer runPeriod;
    private Float minPercent;
    private final IBinder botBinder = new BotBinder();
    private static final String TAG = BotService.class.getSimpleName();
    private boolean autoTrade;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Bot starting", Toast.LENGTH_SHORT).show();
        initFields();
        startTimer();
        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification());
        isRunning = true;
        return START_STICKY;
    }

    private void initFields() {
        this.botTimer = new Timer();
        initFieldsFromPrefs();

    }


    private void initFieldsFromPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        minPercent = Float.valueOf(Objects.requireNonNull(preferences.getString(getString(R.string.min_profit_percent), "3")));
        runPeriod = Integer.valueOf(Objects.requireNonNull(preferences.getString(getString(R.string.service_run_period), "5")));
        autoTrade = preferences.getBoolean(getString(R.string.auto_trade), false);
    }

    private void startTimer() {
        MarketFactory marketFactory = new MarketFactory();

        List<Market> markets = marketFactory.getMarkets(this, PreferenceManager.getDefaultSharedPreferences(this));
        runTimer(markets);
    }

    private void runTimer(List<Market> markets) {
        long period = runPeriod * 1000 * 60;
        BotTimerTask botTimerTask = new BotTimerTask(markets);
        botTimer.scheduleAtFixedRate(botTimerTask, period, period);
        Log.d(TAG, "timer started");
    }

    private Notification buildForegroundNotification() {
        return new NotificationBuilder(this)
                .setTitle("Bot is running")
                .setImportance(NotificationManager.IMPORTANCE_LOW)
                .setChannelName(getApplicationContext().getString(R.string.foreground_channel_name))
                .setChannelId(FOREGROUND_CHANNEL_ID)
                .setNotificationId(FOREGROUND_NOTIFICATION_ID)
                .setColor(R.color.colorPrimary)
                .setExtraFlag("openPairs")
                .build();
    }

    public void update() {
        if (botTimer != null) {
            botTimer.cancel();
            botTimer.purge();
        }
        initFields();
        startTimer();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Bot stopping", Toast.LENGTH_SHORT).show();
        if (botTimer != null) botTimer.cancel();
        stopForeground(true);
        isRunning = false;
        Log.d(TAG, "timer stopped");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return botBinder;
    }

    private class BotTimerTask extends TimerTask {
        private List<Pair> pairs;
        private final List<Market> markets;
        private final Map<String, TradeLimitResponse> minQuantities = new HashMap<>();
        private CoinInfo coinInfo;

        BotTimerTask(List<Market> markets) {
            this.markets = markets;
        }

        @Override
        public void run() {
            Log.d(TAG, "timer running");

            initPairsFromPrefs();

            if (!autoTrade && isNotificationShown()) {
                Log.d(TAG, "notification shown, do nothing");
                return;
            }

            try {
                coinInfo = new CoinInfo(markets);
                if (autoTrade) {
                    initMinQuantities();
                }
            } catch (MarketException e) {
                if (!isNotificationShown())
                    makeNotification("Init min quantities exception", e.getMessage());
                return;
            }
            List<Pair> profitPairs = getProfitPairs(pairs, markets);

            if (!profitPairs.isEmpty() && !autoTrade) {
                makeNotification("Profitable pairs found", getNotificationText(profitPairs));
            }
        }

        private void initPairsFromPrefs() {
            Set<String> coinNames = new BalancePreferences(BotService.this).getItems();
            Set<String> allPairNames = new AllPairsPreferences(BotService.this).getItems();

            pairs = new ArrayList<>();
            for (String baseName : coinNames) {
                for (String marketName : coinNames) {
                    if (!baseName.equals(marketName)) {
                        Pair pair = new Pair(baseName, marketName);
                        String pairName = pair.getName();
                        if (allPairNames.contains(pairName)) {
                            pairs.add(pair);
                        }
                    }
                }
            }
        }

        private synchronized void initMinQuantities() throws MarketException {
            for (Market market : markets) {
                minQuantities.put(market.getMarketName(), market.getMinQuantity());
            }
        }

        private synchronized List<Pair> getProfitPairs(List<Pair> pairs, List<Market> markets) {

            List<Pair> profitPairs = new ArrayList<>();

            for (Pair pair : pairs) {
                if (coinInfo.checkCoinStatus(pair.getBaseName()) &&
                        coinInfo.checkCoinStatus(pair.getMarketName())) {
                    Pair profitPair = profitPercentForPair(pair, markets);
                    if (profitPair != null && profitPair.getPercent() > minPercent) {
                        if (autoTrade) beginTrade(profitPair);
                        profitPairs.add(profitPair);
                    }
                }
            }

            return profitPairs;
        }

        private Pair profitPercentForPair(Pair pair, List<Market> markets) {
            PairResponseEnricher enricher = new PairResponseEnricher(pair);

            if (isTradingNow(pair.getName())) return null;

            for (Market market : markets) {
                OrderBookResponse response;
                try {
                    response = market.getOrderBook(pair.getPairNameForMarket(market.getMarketName()));
                } catch (MarketException e) {
                    if (!isNotificationShown())
                        makeNotification("Get order book exception", e.getMessage());
                    return null;
                }
                if (response != null) {
                    enricher.enrichWithResponse(response);
                }
            }
            return enricher.countPercent().getPair();
        }

        private boolean isTradingNow(String pairName) {
            for (int i = 0; i < 10; i++) {
                if (TradingService.workingOnPair != null && TradingService.workingOnPair.equals(pairName)) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }

        private void beginTrade(Pair pair) {
            Intent intent = new Intent(BotService.this, TradingService.class);
            intent.putExtra(Pair.class.getName(), pair);
            intent.putExtra("minQuantity", getMinQuantity(pair));

            startService(intent);
        }

        private Double getMinQuantity(Pair pair) {
            Double resultQuantity = null;
            for (Market market : markets) {
                String marketName = market.getMarketName();
                TradeLimitResponse response = minQuantities.get(marketName);
                if (response != null) {
                    Double quantity = response.getTradeLimitByName(pair.getPairNameForMarket(marketName));
                    if (resultQuantity == null) {
                        resultQuantity = quantity;
                    } else {
                        if (quantity > resultQuantity) {
                            resultQuantity = quantity;
                        }
                    }
                }
            }
            return resultQuantity;
        }

        private boolean isNotificationShown() {
            StatusBarNotification[] notifications = getSystemService(NotificationManager.class).getActiveNotifications();

            for (StatusBarNotification notification : notifications) {
                if (notification.getId() == NOTIFICATION_ID) {
                    return true;
                }
            }
            return false;
        }

        private void makeNotification(String title, String text) {
            new NotificationBuilder(BotService.this)
                    .setTitle(title)
                    .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                    .setChannelName(getApplicationContext().getString(R.string.channel_name))
                    .setChannelId(CHANNEL_ID)
                    .setNotificationId(NOTIFICATION_ID)
                    .setExtraFlag("openPairs")
                    .setNotificationText(text)
                    .buildAndNotify();
        }

        private String getNotificationText(List<Pair> profitPairs) {
            return profitPairs.stream()
                    .map(pair -> String.format(Locale.getDefault(), "%s - %.2f%s", pair.getName(), pair.getPercent(), System.lineSeparator()))
                    .collect(Collectors.joining());
        }
    }

    public class BotBinder extends Binder {

        public BotService getService() {
            return BotService.this;
        }
    }
}
