package com.chess.cryptobot.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.content.pairs.AllPairsPreferences
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.view.notification.NotificationBuilder
import com.chess.cryptobot.worker.ProfitPairWorker
import java.util.*
import kotlin.collections.ArrayList


class BotService : Service() {
    private var botTimer: Timer? = null
    private var runPeriod: Int = 5
    private val botBinder: IBinder = BotBinder()
    private var webSocketEnabled = false
    private var webSocketOrchestrator: WebSocketOrchestrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "Bot starting", Toast.LENGTH_SHORT).show()
        initFields()
        runTimer()
        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification())
        isRunning = true
        return START_STICKY
    }

    private fun initFields() {
        botTimer = Timer()
        initFieldsFromPrefs()
    }

    private fun initFieldsFromPrefs() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        runPeriod = preferences.getString(getString(R.string.service_run_period), "5")?.toInt() ?: 5
        webSocketEnabled = preferences.getBoolean(getString(R.string.web_socket_enabled), false)
    }

    private fun runTimer() {
        val period = runPeriod * 1000 * 60.toLong()
        val botTimerTask = BotTimerTask()
        botTimer!!.schedule(botTimerTask, 10000, period)
        Log.d(TAG, "timer started")
    }

    private fun buildForegroundNotification(): Notification {
        return NotificationBuilder(this)
                .setTitle("Bot is running")
                .setImportance(NotificationManager.IMPORTANCE_LOW)
                .setChannelName(applicationContext.getString(R.string.foreground_channel_name))
                .setChannelId(FOREGROUND_CHANNEL_ID)
                .setNotificationId(FOREGROUND_NOTIFICATION_ID)
                .setColor(R.color.colorPrimary)
                .setExtraFlag("openPairs")
                .build()
    }

    fun update() {
        if (botTimer != null) {
            botTimer!!.cancel()
            botTimer!!.purge()
        }
        if (webSocketEnabled) {
            webSocketOrchestrator?.disconnectAll()
            webSocketOrchestrator = null
        }
        initFields()
        runTimer()
    }

    override fun onDestroy() {
        Toast.makeText(this, "Bot stopping", Toast.LENGTH_SHORT).show()
        if (botTimer != null) botTimer!!.cancel()
        if (webSocketEnabled) {
            webSocketOrchestrator?.disconnectAll()
            webSocketOrchestrator = null
        }
        stopForeground(true)
        isRunning = false
        Log.d(TAG, "timer stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return botBinder
    }

    private inner class BotTimerTask : TimerTask() {
        private var pairs: MutableList<Pair> = initPairsFromPrefs()

        override fun run() {
            Log.d(TAG, "timer running")

            if (webSocketEnabled) {
                if (webSocketOrchestrator==null)
                    webSocketOrchestrator = WebSocketOrchestrator(this@BotService, pairs)
                webSocketOrchestrator?.subscribeAll()
            } else {
                val profitPairWorker = OneTimeWorkRequest.Builder(ProfitPairWorker::class.java).build()
                WorkManager.getInstance(this@BotService).enqueue(profitPairWorker)
            }
        }

        private fun initPairsFromPrefs(): MutableList<Pair> {
            val coinNames = BalancePreferences(this@BotService).items
            val allPairNames = AllPairsPreferences(this@BotService).items
            val pairs = ArrayList<Pair>()
            if (coinNames != null) {
                for (baseName in coinNames) {
                    for (marketName in coinNames) {
                        if (baseName != marketName) {
                            val pair = Pair(baseName, marketName)
                            val pairName = pair.name
                            if (allPairNames != null) {
                                if (allPairNames.contains(pairName)) {
                                    pairs.add(pair)
                                }
                            }
                        }
                    }
                }
            }
            return pairs
        }
    }

    inner class BotBinder : Binder() {
        val service: BotService
            get() = this@BotService
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 200500
        var isRunning = false
        private const val FOREGROUND_CHANNEL_ID = "bot_channel_id"
        private val TAG = BotService::class.java.simpleName
    }
}