package com.chess.cryptobot.task;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class SerialExecutor implements Executor {
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
    private Runnable mActive;

    public synchronized void execute(final Runnable r) {
        mTasks.offer(() -> {
            try {
                r.run();
            } finally {
                scheduleNext();
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        if ((mActive = mTasks.poll()) != null) {
            THREAD_POOL_EXECUTOR.execute(mActive);
        }
    }
}