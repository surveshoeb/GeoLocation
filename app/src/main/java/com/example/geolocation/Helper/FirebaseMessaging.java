package com.example.geolocation.Helper;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.geolocation.Activity.Home;
import com.example.geolocation.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by surve on 27-Feb-18.
 */

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "Notification";

    Uri defaultSoundUri;
    Bitmap largeIcon;
    android.app.Notification notification;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived: Notification Received");
        Log.d(TAG, "onMessageReceived: "+remoteMessage.toString());

        int requestID = (int) System.currentTimeMillis();

        if (remoteMessage != null) {

            String title = remoteMessage.getData().get("title");
            String description = remoteMessage.getData().get("body");
            String channel = "Invite";

            Intent notificationIntent = null;

            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            largeIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationIntent = new Intent(getApplicationContext(), Home.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannelGroup group = new NotificationChannelGroup(channel, channel);

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(group.getId(), channel, importance);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setShowBadge(true);
                mChannel.setLockscreenVisibility(0);

                notification = new NotificationCompat.Builder(getApplicationContext(), channel)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(largeIcon)
                        .setContentIntent(pendingIntent)
                        .setChannelId(channel)
                        .build();

                notificationManager.createNotificationChannel(mChannel);
            }
            else {
                notification = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(pendingIntent)
                        .setPriority(android.app.Notification.PRIORITY_MAX).build();
            }

            notification.flags |= android.app.Notification.FLAG_ONLY_ALERT_ONCE | android.app.Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(requestID, notification);
        }
    }
}
