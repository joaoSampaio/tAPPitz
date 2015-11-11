package com.tappitz.tappitz.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;

import java.util.UUID;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    public GcmIntentService() {
        super("GcmIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
//        mNotificationManager = (NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, LoginActivity.class), 0);
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_switch_camera)
//                        .setContentTitle("New Notification");
//        mBuilder.setContentIntent(contentIntent);
//
//
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mBuilder.setSound(uri);
//        mBuilder.setVibrate(new long[]{1000, 1000});
//
//
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//        GcmBroadcastReceiver.completeWakefulIntent(intent);





        Bundle extras = intent.getExtras();
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        if(title == null)
            title = "nao veio titulo";
        if(message == null)
            message = "nao veio msg";


        int icon = R.drawable.ic_switch_camera;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "Custom Notification", when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, icon);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, message);
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, ScreenSlidePagerActivity.class);

        String xId = UUID.randomUUID().toString();
        notificationIntent.putExtra("x_id", xId);
        notificationIntent.setAction(xId);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtras(extras);
        //notificationIntent.putExtra("action", intent.getStringExtra("action"));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags =  Notification.FLAG_AUTO_CANCEL;
        //notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound

        mNotificationManager.notify(1, notification);



        GcmBroadcastReceiver.completeWakefulIntent(intent);









    }






//    public void notifyUser(Context context,String data){
//        Intent intent = new Intent(context, NotificationActivity.class);
//        intent.putExtra("data", data);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setAutoCancel(true);
//        builder.setContentTitle("New Notification");
//        builder.setContentIntent(pendingIntent);
//        builder.setContentText(data);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        builder.setSound(uri);
//        builder.setVibrate(new long[]{1000, 1000});
//        notificationManager.notify(countNotification++, builder.build());
//    }




}