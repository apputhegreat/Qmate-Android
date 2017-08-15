package com.quotemate.qmate.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.quotemate.qmate.MainActivity;
import com.quotemate.qmate.R;

/**
 * Created by anji kinnara on 7/15/17.
 */

public class NotificationQOD extends IntentService {
    private static final int NOTIFICATION_ID = 3;
    public NotificationQOD() {
        super(IntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Notification.Builder builder = new Notification.Builder(NotificationQOD.this);
        builder.setContentTitle("Quote of the day");
        builder.setContentText(intent.getStringExtra("body"));
        builder.setSmallIcon(R.mipmap.qtoniq);
        Intent notifyIntent = new Intent(NotificationQOD.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationQOD.this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationQOD.this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
