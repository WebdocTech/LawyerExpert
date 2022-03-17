package com.webdoc.lawyerexpertandroid.FirebaseService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.webdoc.lawyerexpertandroid.Agora.IncomingVideoCall.IncomingVideoCallActivity;
import com.webdoc.lawyerexpertandroid.Essentials.Constants;
import com.webdoc.lawyerexpertandroid.Essentials.Global;
import com.webdoc.lawyerexpertandroid.Preferences.Preferences;
import com.webdoc.lawyerexpertandroid.R;

import java.util.Map;

public class FcmListenerService extends FirebaseMessagingService {

    public static NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String title, body, channel;
    Preferences preferences;

    public String INCOMING_CALL_CHANNEL_ID = "Agora Incoming Call Push Notification Channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        preferences = new Preferences(this);
        Map<String, String> dataMap = remoteMessage.getData();

        if (dataMap.get("title") != null) {
            title = dataMap.get("title");

            if (title.equals(Constants.VIDEO_CALL_TITLE)) {
                body = dataMap.get("body");
                channel = dataMap.get("channel");

                createCallNotification(body);
            }
        }
    }

    private void createNotificationChannel(int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.ringing);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Agora Incoming Call";
            String description = "Incoming Agora Push Notifications.";
            NotificationChannel channel = new NotificationChannel(INCOMING_CALL_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(sound, attributes); // This is IMPORTANT
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createCallNotification(String userId) {

        createNotificationChannel(NotificationManager.IMPORTANCE_MAX);
        PendingIntent contentIntent = null;

        if (title.equals(Constants.VIDEO_CALL_TITLE)) {
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), IncomingVideoCallActivity.class), 0);
        }

        builder = new NotificationCompat.Builder(getApplicationContext(), INCOMING_CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(userId)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.ringing))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Notification noti = builder.build();

        noti.flags = Notification.FLAG_INSISTENT;

        if (title.equals(Constants.VIDEO_CALL_TITLE)) {
            mNotificationManager.notify(2, noti);
        }

        Global.channel = channel;
        Global.callerID = body;

        if (title.equals(Constants.VIDEO_CALL_TITLE)) {
            Intent intent = new Intent(this, IncomingVideoCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getApplicationContext().startActivity(intent);
        }

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "WAKE UP SCREEN");
        wakeLock.acquire();
    }
}


