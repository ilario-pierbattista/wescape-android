package com.dii.ids.application.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dii.ids.application.R;
import com.dii.ids.application.main.authentication.AuthenticationActivity;
import com.dii.ids.application.main.navigation.HomeFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class WescapeMessagingService extends FirebaseMessagingService {
    private static final String TAG = WescapeMessagingService.class.getName();
    private static final String KEY_EMERGENCY = "emergency";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Notification Click Action: " + remoteMessage.getNotification().getClickAction());

        // Manage notification when the app is in foreground
        for (String key : remoteMessage.getData().keySet()) {
            if (key.equals(KEY_EMERGENCY)) {
                updateMyActivity(this);
            }
            Log.d(TAG, "Key: " + key + " Value: " + remoteMessage.getData().get(key));
        }

        // Calling method to generate notification
        // Manage notification when the app is in background.
        // An Intent Filter has been defined in Manifest.xml
        sendNotification(remoteMessage);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(RemoteMessage message) {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void updateMyActivity(Context context) {
        Log.d(TAG, "updateMyActivity");
        Intent intent = new Intent(HomeFragment.EMERGENCY_ACTION);
        intent.setAction(HomeFragment.EMERGENCY_ACTION);

        //send broadcast
        context.sendBroadcast(intent);
    }
}
