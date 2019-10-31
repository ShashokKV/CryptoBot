package com.chess.cryptobot.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.core.app.NotificationCompat;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.content.pairs.AllPairsPreferences;
import com.chess.cryptobot.enricher.PairResponseEnricher;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.view.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class BotService extends Service {
    public static final int NOTIFICATION_ID = 100500;
    public static boolean isRunning;
    private final static String CHANNEL_ID = "profit_pairs_channel_id";
    private Timer botTimer;
    private List<Pair> pairs;
    private Integer runPeriod;
    private Float minPercent;
    private Float fee;
    private final IBinder botBinder = new BotBinder();
    private static final String TAG = BotService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Toast.makeText(this, "Bot starting", Toast.LENGTH_SHORT).show();
        initFields();
        startTimer();
        startForeground(200500, buildForegroundNotification());
        isRunning = true;
        return START_STICKY;
    }

    private void initFields() {
        this.botTimer = new Timer();
        initFieldsFromPrefs();
        initPairsFromPrefs();
    }

    private void initPairsFromPrefs() {
        Set<String> coinNames = new BalancePreferences(this).getItems();
        Set<String> allPairNames = new AllPairsPreferences(this).getItems();

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

    private void initFieldsFromPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        minPercent = Float.valueOf(Objects.requireNonNull(preferences.getString(getString(R.string.min_profit_percent), "3")));
        fee = Float.valueOf(getString(R.string.bittrex_fee)) + Float.valueOf(getString(R.string.livecoin_fee));
        runPeriod = Integer.valueOf(Objects.requireNonNull(preferences.getString(getString(R.string.service_run_period), "5")));
    }

    private void startTimer() {
        MarketFactory marketFactory = new MarketFactory();

        List<Market> markets = marketFactory.getMarkets(this, PreferenceManager.getDefaultSharedPreferences(this));
        runTimer(pairs, markets);
    }

    private void runTimer(List<Pair> pairs, List<Market> markets) {
        long period = runPeriod*1000*60;
        BotTimerTask botTimerTask = new BotTimerTask(pairs, markets);
        botTimer.scheduleAtFixedRate(botTimerTask, 1000, period);
        Log.d(TAG, "timer started");
    }

    private Notification buildForegroundNotification() {
        PendingIntent pendingIntent = getPendingIntent();

        return new NotificationCompat.Builder(BotService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.round_monetization_on_24)
                .setContentTitle("Bot is running")
                .setColorized(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setColor(getResources().getColor(R.color.colorPrimary, null))
                .setContentIntent(pendingIntent)
                .setSound(null)
                .build();

    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(BotService.this, MainActivity.class);
        intent.putExtra("openPairs", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        return PendingIntent.getActivity(BotService.this, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void update() {
        if (botTimer!=null) botTimer.cancel();
        initFields();
        startTimer();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Bot stopping", Toast.LENGTH_SHORT).show();
        botTimer.cancel();
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
        private List<Market> markets;

        BotTimerTask(List<Pair> pairs, List<Market> markets) {
            this.pairs = pairs;
            this.markets = markets;
        }

        @Override
        public void run() {
            Log.d(TAG, "timer running");
            if (isNotificationShown()) {
                Log.d(TAG, "notification shown, do nothing");
                return;
            }
            List<Pair> profitPairs = getProfitPairs(pairs, markets);

            if (!profitPairs.isEmpty()) {
                makeNotification(profitPairs);
            }
        }

        private List<Pair> getProfitPairs(List<Pair> pairs, List<Market> markets) {
            List<Pair> profitPairs = new ArrayList<>();
            pairs.forEach(pair -> {
                pair = profitPercentForPair(pair, markets);
                Log.d(TAG, "pair profit percent "+pair.getPercent());
                if (pair.getPercent()>minPercent) {
                    profitPairs.add(pair);
                }
            });
            return profitPairs;
        }

        private Pair profitPercentForPair(Pair pair, List<Market> markets) {
            PairResponseEnricher enricher = new PairResponseEnricher(pair);
            for(Market market: markets) {
                OrderBookResponse response = null;
                try {
                    response = market.getOrderBook(pair.getPairNameForMarket(market.getMarketName()));
                } catch (MarketException ignored) {}
                if (response!=null) {
                    enricher.enrichWithResponse(response);
                }
            }
            return enricher.countPercent(fee).getPair();
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

        private void makeNotification(List<Pair> profitPairs) {
            String text = getNotificationText(profitPairs);
            NotificationManager notificationManager = getNotificationManager();
            notificationManager.notify(NOTIFICATION_ID, buildNotification(text));
        }

        private String getNotificationText(List<Pair> profitPairs) {
            return profitPairs.stream()
                    .map(pair -> String.format(Locale.getDefault(),"%s - %.2f%s", pair.getName(), pair.getPercent(), System.lineSeparator()))
                    .collect(Collectors.joining());
        }

        private NotificationManager getNotificationManager() {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            return notificationManager;
        }

        private Notification buildNotification(String notificationText) {
            PendingIntent pendingIntent = getPendingIntent();

            return new Notification.Builder(BotService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.round_monetization_on_24)
                    .setContentTitle("Profitable pairs found")
                    .setStyle(new Notification.BigTextStyle().bigText(notificationText))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }
    }

    public class BotBinder extends Binder {

        public BotService getService() {
            return BotService.this;
        }
    }
}
