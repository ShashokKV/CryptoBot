package com.chess.cryptobot.view.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.chess.cryptobot.R;
import com.chess.cryptobot.view.MainActivity;

public class NotificationBuilder {
    private final Context context;
    private int notificationId;
    private String channelId;
    private String channelName;
    private String text;
    private String extraFlag;
    private int importance;
    private String title;
    private Integer color;
    private final NotificationManager notificationManager;

    public NotificationBuilder(Context context) {
        this.context = context;
        notificationManager = context.getSystemService(NotificationManager.class);
    }

    public NotificationBuilder setNotificationId(int id) {
        this.notificationId = id;
        return this;
    }

    public NotificationBuilder setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public NotificationBuilder setNotificationText(String text) {
        this.text = text;
        return this;
    }

    public NotificationBuilder setExtraFlag(String flag) {
        this.extraFlag = flag;
        return this;
    }

    public NotificationBuilder setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public NotificationBuilder setImportance(int importance) {
        this.importance = importance;
        return this;
    }

    public NotificationBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public NotificationBuilder setColor(Integer color) {
        this.color = color;
        return this;
    }

    public void buildAndNotify() {
        notificationManager.notify(notificationId, build());
    }

    public Notification build() {

        createNotificationChannelIfNotExist(notificationManager);
        return buildNotification();

    }

    private void createNotificationChannelIfNotExist(NotificationManager notificationManager) {
        NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
        if (channel == null) {
            channel = new NotificationChannel(channelId, channelName, importance);
            channel.enableVibration(false);
            channel.enableLights(false);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        PendingIntent pendingIntent = getPendingIntent();

        Notification.Builder builder = new Notification.Builder(context, channelId)
                .setSmallIcon(R.drawable.round_monetization_on_24)
                .setContentTitle(title)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE);
        if (this.color != null) {
            builder.setColorized(true)
                    .setColor(context.getResources().getColor(R.color.colorPrimary, null));
        }

        return builder.build();
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extraFlag != null) intent.putExtra(extraFlag, true);
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        return PendingIntent.getActivity(context, uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
