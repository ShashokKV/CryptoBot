package com.chess.cryptobot.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.chess.cryptobot.R;
import com.chess.cryptobot.enricher.PairResponseEnricher;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.view.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MarketWorker extends Worker {
    private Float fee;
    private Integer minPercent;
    private final static String CHANNEL_ID = "profit_pairs_channel_id";

    public MarketWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        minPercent = inputData.getInt("min_percent", 3);
        fee = inputData.getFloat("fee", 0.38f);
        List<String> availablePairs = Arrays.asList(inputData.getStringArray("available_pairs"));
        List<Pair> pairs = initPairsFromNames(availablePairs);

        MarketFactory marketFactory = new MarketFactory();
        Context context = getApplicationContext();

        List<Market> markets = marketFactory.getMarkets(context, PreferenceManager.getDefaultSharedPreferences(context));
        List<Pair> profitPairs = getProfitPairs(pairs, markets);

        if (!profitPairs.isEmpty()) {
            makeNotification(profitPairs);
        }

        return Result.retry();
    }



    private List<Pair> initPairsFromNames(List<String> pairNames) {
        ArrayList<Pair> pairs = new ArrayList<>();
        pairNames.forEach(pairName -> pairs.add(Pair.fromPairName(pairName)));
        return pairs;
    }

    private List<Pair> getProfitPairs(List<Pair> pairs, List<Market> markets) {
        List<Pair> profitPairs = new ArrayList<>();
        pairs.forEach(pair -> {
            pair = profitPercentForPair(pair, markets);
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

    private void makeNotification(List<Pair> profitPairs) {
        String text = getNotificationText(profitPairs);
        NotificationManagerCompat notificationManager = getNotificationManager();
        notificationManager.notify(100500, buildNotification(text));
    }

    private String getNotificationText(List<Pair> profitPairs) {
        return profitPairs.stream()
                .map(pair -> String.format(Locale.getDefault(),"%s - %f%s", pair.getName(), pair.getPercent(), System.lineSeparator()))
                .collect(Collectors.joining());
    }

    private NotificationManagerCompat getNotificationManager() {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
        NotificationManagerCompat notificationManager = getApplicationContext().getSystemService(NotificationManagerCompat.class);
        notificationManager.createNotificationChannel(channel);
        return notificationManager;
    }

    private Notification buildNotification(String notificationText) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.round_monetization_on_24)
                .setContentTitle("Profitable pairs found")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
    }
}
