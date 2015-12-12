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
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.GetColor;
import com.tappitz.tappitz.util.ModelCache;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

        saveVotesOffline(data);

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


//        Intent notificationIntent = new Intent(this, ScreenSlidePagerActivity.class);
//
//        String xId = UUID.randomUUID().toString();
//        notificationIntent.putExtra("x_id", xId);
//        notificationIntent.setAction(xId);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        notificationIntent.putExtras(extras);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Notification notification = new Notification.Builder(getApplicationContext())
//                .setContentTitle("Title")
//                .setContentText("Text")
//                .setSmallIcon(R.drawable.ic_stat_logo_tappitz)
//                .setWhen(System.currentTimeMillis())
//                .setContentIntent(contentIntent)
//                .getNotification();
//
//        notification.flags =  Notification.FLAG_AUTO_CANCEL;
//        //notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
//        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
//        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
//
//        //startForeground(1,notification);
//        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, notification);


    //"De quem, a que horas, informação da cor votada e o texto que eventualmente tenha escrito"...



        int icon = R.drawable.ic_stat_logo_tappitz;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "tAPPitz", when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);

        String action = extras.getString("action", "");

        //String date = DateFormat.getTimeInstance().format(new Date(when));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(when);
        System.out.println(sdf.format(resultdate));
        String date = sdf.format(resultdate);


        Log.d("myapp_new", "date:" + date);
        contentView.setImageViewResource(R.id.image, icon);
        contentView.setTextViewText(R.id.date, date);
        contentView.setTextColor(R.id.title, getResources().getColor(R.color.white));
        if(action.equals(Global.NEW_PICTURE_VOTE)) {
            String authorName = extras.getString("authorName");
            String comment = extras.getString("comment", "Has sent you a vote.");
            if(comment.length() > 30){
                comment = comment.substring(0,30) + "...";
            }

            //int vote = extras.getInt("vote");
            String vote = extras.getString("vote", "0");
            Log.d("myapp_new", "vote:" + vote);
            contentView.setTextViewText(R.id.title, authorName +" has voted!");
            contentView.setTextViewText(R.id.text, comment);
            contentView.setTextColor(R.id.title, getResources().getColor(GetColor.getColor(vote)));
        }else if(action.equals(Global.NEW_PICTURE_RECEIVED)) {
            String authorName = extras.getString("authorName");
            String comment = extras.getString("comment", "");
            if(comment.length() > 30){
                comment = comment.substring(0,30) + "...";
            }

            contentView.setTextViewText(R.id.title, authorName + " has sent you a picture!");
            contentView.setTextViewText(R.id.text, comment);
        }else {
            contentView.setTextViewText(R.id.title, "tAPPitz");
            contentView.setTextViewText(R.id.text, "WoW!! we have news");

        }

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

    private void saveVotesOffline(Bundle extras){
        //authorName=Miguel Sampaio, action=NEW_PICTURE_VOTE, pictureId=272, date=Sun Dec 06 17:10:33 UTC 2015, vote=1, comment=, authorId=34
        String pictureId = extras.getString("pictureId", "-1");
        String voteAuthorName = extras.getString("authorName", "");
        String comment = extras.getString("comment", "");
        String vote = extras.getString("vote", "-1");
        String votedDate = extras.getString("date", "");
        int id = Integer.parseInt(pictureId);
        int voteInt = Integer.parseInt(vote);

        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);
//        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(votedDate);

            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            votedDate = dfmt.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);
        Log.d("updateMyActivity","..." + date);


        //DateFormat dfmt = new SimpleDateFormat("EEEE, d 'de' MMMM 'às' HH:mm");
        //Log.d("updateMyActivity", "..." + dfmt.format(date));

        //nao recebemos um vote
        if(voteInt < 0 || id < 0)
            return;

        Log.d("myapp", "**--loadOffline:");
            Context ctx = getApplicationContext();
            List<Comment> comments = new ModelCache<List<Comment>>().loadModel(ctx,new TypeToken<List<Comment>>(){}.getType(), Global.OFFLINE_VOTE+id);
            if(comments != null && comments.size() > 0 && comments.get(0) instanceof Comment) {
                Log.d("myapp", "**--loadOffline: inside ");


            }else{
                comments = new ArrayList<>();
            }
        comments.add(0, new Comment(voteInt, comment, votedDate, voteAuthorName));
        new ModelCache<List<Comment>>().saveModel(ctx, comments, Global.OFFLINE_VOTE + id);
        extras.putString("date", votedDate);
    }






}