package com.chess.cryptobot.worker;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestWorkerBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class BalanceWorkerTest {
    private Context mContext;
    private Executor mExecutor;

    @Before
    public void setUp() {
        mContext = ApplicationProvider.getApplicationContext();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    @Test
    public void testMarketWorker() {

        BalanceWorker worker =
                TestWorkerBuilder.from(mContext,
                        BalanceWorker.class,
                        mExecutor)
                        .build();

        ListenableWorker.Result result = worker.doWork();
        assertThat(result, is(ListenableWorker.Result.success()));
    }
}