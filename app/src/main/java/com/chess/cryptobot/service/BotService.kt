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
import com.chess.cryptobot.R
import com.chess.cryptobot.view.notification.NotificationBuilder
import java.util.*


class BotService : Service() {
    private var botTimer: Timer? = null
    private var runPeriod: Int = 5
    private val botBinder: IBinder = BotBinder()
    private var webSocketEnabled = false

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
        botTimer!!.schedule(botTimerTask, period, period)
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
        initFields()
        runTimer()
    }

    override fun onDestroy() {
        Toast.makeText(this, "Bot stopping", Toast.LENGTH_SHORT).show()
        if (botTimer != null) botTimer!!.cancel()
        stopForeground(true)
        isRunning = false
        Log.d(TAG, "timer stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return botBinder
    }

    private inner class BotTimerTask : TimerTask() {

        override fun run() {
            Log.d(TAG, "timer running")

           startService(Intent(this@BotService, ProfitPairService::class.java))
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