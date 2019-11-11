package com.chess.cryptobot.worker;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestWorkerBuilder;

import com.chess.cryptobot.content.pairs.AllPairsPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class MarketWorkerTest {
    private Context mContext;
    private Executor mExecutor;

    @Before
    public void setUp() {
        mContext = ApplicationProvider.getApplicationContext();
        mExecutor = Executors.newSingleThreadExecutor();
        initAvailablePairs();
    }

    @Test
    public void testMarketWorker() {

        MarketWorker worker =
                TestWorkerBuilder.from(mContext,
                        MarketWorker.class,
                        mExecutor)
                        .build();

        ListenableWorker.Result result = worker.doWork();
        assertThat(result, is(ListenableWorker.Result.success()));
    }

    private void initAvailablePairs() {
        AllPairsPreferences preferences = new AllPairsPreferences(ApplicationProvider.getApplicationContext());

        HashSet<String> availablePairs = new HashSet<>();
        availablePairs.add("BTC/ZEC");
        availablePairs.add("BTC/DASH");
        availablePairs.add("BTC/XRP");
        availablePairs.add("BTC/GRS");
        availablePairs.add("BTC/DGB");
        availablePairs.add("BTC/WAVES");

        preferences.setItems(availablePairs);
    }

}