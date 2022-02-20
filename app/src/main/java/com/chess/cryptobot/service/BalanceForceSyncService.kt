package com.chess.cryptobot.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.chess.cryptobot.exceptions.InitializationException
import com.chess.cryptobot.worker.BalanceSyncWorker

class BalanceForceSyncService : Service() {
    private val balanceForceSyncServiceBinder: IBinder = BalanceForceSyncServiceBinder()
    private val tag = BalanceForceSyncService::class.qualifiedName

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val state = super.onStartCommand(intent, flags, startId)
        try{
            if (intent==null) {
                throw InitializationException("Intent")
            }

            val coinNames = intent.getStringArrayListExtra("coinNames")
                ?: throw InitializationException("coinNames")
            val forceUpdate = intent.getBooleanExtra("forceUpdate", true)
            val makeNotifications = intent.getBooleanExtra("makeNotifications", true)
            val dataBuilder = Data.Builder()
            dataBuilder.putStringArray("coinNames", coinNames.toTypedArray())
            dataBuilder.putBoolean("forceUpdate",forceUpdate)
            dataBuilder.putBoolean("makeNotifications", makeNotifications)

            val balanceSyncWorker = OneTimeWorkRequest.Builder(BalanceSyncWorker::class.java)
                .setInputData(dataBuilder.build())
                .build()

            WorkManager.getInstance(this).enqueue(balanceSyncWorker)
        } catch (e: Exception) {
            Log.e(tag, Log.getStackTraceString(e))
        }
        
        return state
    }

    override fun onBind(intent: Intent?): IBinder {
       return balanceForceSyncServiceBinder
    }

    inner class BalanceForceSyncServiceBinder : Binder() {
        val service: BalanceForceSyncService
            get() = this@BalanceForceSyncService
    }
}