package com.chess.cryptobot.view.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.chess.cryptobot.R
import com.chess.cryptobot.view.MainActivity

class NotificationBuilder(private val context: Context) {
    private var notificationId = 0
    private var channelId: String? = null
    private var channelName: String? = null
    private var text: String? = null
    private var extraFlag: String? = null
    private var importance = 0
    private var title: String? = null
    private var color: Int? = null
    private val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)

    fun setNotificationId(id: Int): NotificationBuilder {
        notificationId = id
        return this
    }

    fun setChannelId(channelId: String?): NotificationBuilder {
        this.channelId = channelId
        return this
    }

    fun setNotificationText(text: String?): NotificationBuilder {
        this.text = text
        return this
    }

    fun setExtraFlag(flag: String?): NotificationBuilder {
        extraFlag = flag
        return this
    }

    fun setChannelName(channelName: String?): NotificationBuilder {
        this.channelName = channelName
        return this
    }

    fun setImportance(importance: Int): NotificationBuilder {
        this.importance = importance
        return this
    }

    fun setTitle(title: String?): NotificationBuilder {
        this.title = title
        return this
    }

    fun setColor(color: Int?): NotificationBuilder {
        this.color = color
        return this
    }

    fun buildAndNotify() {
        notificationManager.notify(notificationId, build())
    }

    fun build(): Notification {
        createNotificationChannelIfNotExist(notificationManager)
        return buildNotification()
    }

    private fun createNotificationChannelIfNotExist(notificationManager: NotificationManager) {
        var channel = notificationManager.getNotificationChannel(channelId)
        if (channel == null) {
            channel = NotificationChannel(channelId, channelName, importance)
            channel.enableVibration(false)
            channel.enableLights(false)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val pendingIntent = pendingIntent
        val builder = Notification.Builder(context, channelId)
                .setSmallIcon(R.drawable.round_monetization_on_24)
                .setContentTitle(title)
                .setStyle(Notification.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE)
        if (color != null) {
            builder.setColorized(true)
                    .setColor(context.resources.getColor(R.color.colorPrimary, null))
        }
        return builder.build()
    }

    private val pendingIntent: PendingIntent
        get() {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (extraFlag != null) intent.putExtra(extraFlag, true)
            val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
            return PendingIntent.getActivity(context, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

}