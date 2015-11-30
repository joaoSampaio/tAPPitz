package com.tappitz.tappitz.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmListenerService;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;

import java.util.UUID;

/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {


    public GcmService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("updateMyActivity", "From: " + from);
        Log.d("updateMyActivity", "Message: " + message);

        Log.d("updateMyActivity", "extras.size():" + data.size());
        Log.d("updateMyActivity", "extras.toString():" + data.toString());

        for (String key : data.keySet()) {
            Object value = data.get(key);
            Log.d("updateMyActivity", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        //String message = data.getString("message");
//        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_media_play)
//                .setContentTitle("Test")
//                .setContentText(message);
//        notificationManager.notify(1, mBuilder.build());



        sendNotification("Received GCM Message: ", data);
        updateMyActivity(data);
    }

//    @Override
//    public void onDeletedMessages() {
//        sendNotification("Deleted messages on server");
//    }
//
//    @Override
//    public void onMessageSent(String msgId) {
//        sendNotification("Upstream message sent. Id=" + msgId);
//    }
//
//    @Override
//    public void onSendError(String msgId, String error) {
//        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
//    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.


    private void sendNotification(String message, Bundle extras) {

        int icon = R.drawable.ic_switch_camera;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "Custom Notification", when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, icon);
        contentView.setTextViewText(R.id.title, "GCM Message");
        contentView.setTextViewText(R.id.text, message);
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, ScreenSlidePagerActivity.class);

        String xId = UUID.randomUUID().toString();
        notificationIntent.putExtra("x_id", xId);
        notificationIntent.setAction(xId);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtras(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags =  Notification.FLAG_AUTO_CANCEL;
        //notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound

        mNotificationManager.notify(1, notification);
    }

    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    void updateMyActivity( Bundle extras) {

        Log.d("updateMyActivity", "extras.size():" + extras.size());
        Log.d("updateMyActivity", "extras.toString():" + extras.toString());

        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d("updateMyActivity", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        Intent intent = new Intent("tAPPitz_1");
        Log.d("myapp3", "updateMyActivity");
        //put whatever data you want to send, if any
        intent.putExtras(extras);
        //send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}