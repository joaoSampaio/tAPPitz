package com.tappitz.app.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.app.AppController;
import com.tappitz.app.model.Comment;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.model.UnseenNotifications;
import com.tappitz.app.ui.MainActivity;
import com.tappitz.app.util.ModelCache;
import com.tappitz.app.util.NotificationCount;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {

    private ReentrantLock lock;
    public GcmService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {

        if(lock == null){
            Log.d("updateMyActivity", "lock == null");
            lock = new ReentrantLock();
        }

        lock.lock();  // block until condition holds
        try {
            String message = data.getString("message");
            Log.d("updateMyActivity", "From: " + from);
            Log.d("updateMyActivity", "Message: " + message);

            for (String key : data.keySet()) {
                Log.d("notification", " " + key + " => " + data.get(key) + ";");
            }

            String action = "";

            action = data.getString("action", "");
            UnseenNotifications unseenNotifications = UnseenNotifications.load();
            if(action != null) {
                switch (action) {
                    case Global.NEW_PICTURE_RECEIVED:
                        saveReceivedPictureOffline(data);
                        break;
                    case Global.NEW_PICTURE_VOTE:
                        saveVotesOffline(data);

                        break;
                }
            }
            saveUnseenNotification(data);
            updateMyActivity(data);
            sendNotification( data);
        } finally {
            lock.unlock();
        }



    }

    private void sendNotification( Bundle extras) {
        Log.d("sendNotification", "**sendNotification**** ");

        //
        int totalNotifications = addNotificationCount();


        int notifyID = Global.NOTIFICATION_ID;
        String action = extras.getString("action", "");
        String title = "";
        String message = "";




        if(action.equals(Global.NEW_PICTURE_VOTE)) {
            String authorName = extras.getString("authorName");
            String comment = extras.getString("comment", "Has sent you a vote.");
            if(comment.length() > 30){
                comment = comment.substring(0,30) + "...";
            }

            String vote = extras.getString("vote", "0");
            title =  authorName +" has voted!";
            message = comment;

        }else if(action.equals(Global.NEW_PICTURE_RECEIVED)) {
            String authorName = extras.getString("authorName");
            String comment = extras.getString("comment", "");
            if(comment.length() > 30){
                comment = comment.substring(0,30) + "...";
            }
            title =  authorName +" has sent you a picture!";
            message = comment;
        }else {
            title =  "tAPPitz";
            message = "WoW!! we have news";
        }

        if(totalNotifications > 1){
            title =  "tAPPitz";
            message = "WoW!! we have news";
            extras.putString("action", "home");

        }
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_notification_big);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_notification_big)
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setCategory("CATEGORY_SOCIAL");

        if(totalNotifications <= 2)
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        //                .setLargeIcon(getBitmapFromURL("https://graph.facebook.com/v2.5/592135650941808/picture?width=100&height=100"))

//        Log.d("sendNotification", "**sendNotification**** " + teste);
//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        String[] events = new String[6];
//        // Sets a title for the Inbox in expanded layout
//        inboxStyle.setBigContentTitle("tAppitz news:");
//
//        events[0] = "teste 1 bla bla bla oi";
//        events[1] = "teste 2 bla bla bla adeus";
//        events[2] = "teste 3 bla bla bla";
//        events[3] = "teste 4 bla bla bla";
//        events[4] = "teste 5 bla bla bla";
//        events[5] = "teste 6 bla bla bla";
//        Log.d("sendNotification", "**sendNotification**** " + teste);
//
//        // Moves events into the expanded layout
//        for (int i=0; i < events.length; i++) {
//
//            inboxStyle.addLine(events[i]);
//        }

        // Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(this, MainActivity.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.putExtras(extras);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(notifyPendingIntent);
//        mBuilder.setStyle(inboxStyle);
        mBuilder.setNumber(totalNotifications);
        notificationManager.notify(notifyID, mBuilder.build());

        Log.d("sendNotification", "**sendNotification end**** ");


//
//        int icon = R.drawable.ic_stat_logo_tappitz;
//        long when = System.currentTimeMillis();
//        Notification notification = new Notification(icon, "tAPPitz", when);
//
//        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
//
//        String action = extras.getString("action", "");
//
//        //String date = DateFormat.getTimeInstance().format(new Date(when));
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        Date resultdate = new Date(when);
//        System.out.println(sdf.format(resultdate));
//        String date = sdf.format(resultdate);
//
//
//        Log.d("myapp_new", "date:" + date);
//        contentView.setImageViewResource(R.id.image, icon);
//        contentView.setTextViewText(R.id.date, date);
//        contentView.setTextColor(R.id.title, getResources().getColor(R.color.white));
//        if(action.equals(Global.NEW_PICTURE_VOTE)) {
//            String authorName = extras.getString("authorName");
//            String comment = extras.getString("comment", "Has sent you a vote.");
//            if(comment.length() > 30){
//                comment = comment.substring(0,30) + "...";
//            }
//
//            //int vote = extras.getInt("vote");
//            String vote = extras.getString("vote", "0");
//            Log.d("myapp_new", "vote:" + vote);
//            contentView.setTextViewText(R.id.title, authorName +" has voted!");
//            contentView.setTextViewText(R.id.text, comment);
//            contentView.setTextColor(R.id.title, getResources().getColor(GetColor.getColor(vote)));
//        }else if(action.equals(Global.NEW_PICTURE_RECEIVED)) {
//            String authorName = extras.getString("authorName");
//            String comment = extras.getString("comment", "");
//            if(comment.length() > 30){
//                comment = comment.substring(0,30) + "...";
//            }
//
//            contentView.setTextViewText(R.id.title, authorName + " has sent you a picture!");
//            contentView.setTextViewText(R.id.text, comment);
//        }else {
//            contentView.setTextViewText(R.id.title, "tAPPitz");
//            contentView.setTextViewText(R.id.text, "WoW!! we have news");
//
//        }
//
//        notification.contentView = contentView;
//
//
//
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//
//        String xId = UUID.randomUUID().toString();
//        notificationIntent.putExtra("x_id", xId);
//        notificationIntent.setAction(xId);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        notificationIntent.putExtras(extras);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        notification.contentIntent = contentIntent;
//
//        notification.flags =  Notification.FLAG_AUTO_CANCEL;
//        //notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
//        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
//        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
//
//        mNotificationManager.notify(1, notification);
    }

    public Bitmap getBitmapFromURL(String strURL) {
        Log.d("sendNotification", "**getBitmapFromURL**** ");
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("sendNotification", "**erro**** ");
            return null;
        }
    }




    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    void updateMyActivity( Bundle extras) {




        Intent intent = new Intent("tAPPitz_1");
        Log.d("GcmService", "updateMyActivity");
        //put whatever data you want to send, if any
        intent.putExtras(extras);
        //send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private int addNotificationCount(){
        return NotificationCount.addCount(getApplicationContext());
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
        boolean exists = Comment.alreadyExistsAuthor(comments, voteAuthorName);
        if(!exists) {
            comments.add(0, new Comment(voteInt, comment, votedDate, voteAuthorName));
            new ModelCache<List<Comment>>().saveModel(ctx, comments, Global.OFFLINE_VOTE + id);
        }
        extras.putString("date", votedDate);
    }


    private void saveReceivedPictureOffline(Bundle extras){
        Log.d("myapp", "**--saveReceivedPictureOffline ");
//        date => Sun Mar 13 21:56:26 UTC 2016;
        String sentDate = extras.getString("date", "");;
        String myComment = "", votedDate = "";
        String pictureId = extras.getString("pictureId", "-1");
        String pictureSentence = extras.getString("comment", "");
        String authorName = extras.getString("authorName", "");

        int pictureIdInt = Integer.parseInt(pictureId);
        boolean isHasVoted = false;

        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(sentDate);

            DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sentDate = dfmt.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ReceivedPhoto tmp = new ReceivedPhoto(pictureIdInt, pictureSentence,
                authorName, sentDate, isHasVoted,
                votedDate, myComment, 0);
        tmp.setIsVoteTemporary(false);





        AppController.getInstance().addToInbox(tmp);


    }


    private void saveUnseenNotification(Bundle extras){
        String action = "",pictureId;
        int pictureIdInt;
        action = extras.getString("action", "");
        UnseenNotifications unseenNotifications = UnseenNotifications.load();
        if(action != null) {
            switch (action) {
                case Global.NEW_PICTURE_RECEIVED:
                    pictureId = extras.getString("pictureId", "-1");
                    pictureIdInt = Integer.parseInt(pictureId);
                    unseenNotifications.addReceivedPhoto(pictureIdInt);
                    break;
                case Global.NEW_PICTURE_VOTE:
                    pictureId = extras.getString("pictureId", "-1");
                    String vote = extras.getString("vote", "-1");
                    pictureIdInt = Integer.parseInt(pictureId);
                    int voteInt = Integer.parseInt(vote);
                    unseenNotifications.addCommentPhoto(pictureIdInt, voteInt);
                    break;
            }
        }
        unseenNotifications.save();
    }



}