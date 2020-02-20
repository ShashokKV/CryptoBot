package com.chess.cryptobot.task

import android.os.AsyncTask
import java.util.*
import java.util.concurrent.Executor

class SerialExecutor : Executor {
    private val mTasks = ArrayDeque<Runnable>()
    private var mActive: Runnable? = null
    @Synchronized
    override fun execute(r: Runnable) {
        mTasks.offer(Runnable {
            try {
                r.run()
            } finally {
                scheduleNext()
            }
        })
        if (mActive == null) {
            scheduleNext()
        }
    }

    @Synchronized
    private fun scheduleNext() {
        if (mTasks.poll().also { mActive = it } != null) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(mActive)
        }
    }
}