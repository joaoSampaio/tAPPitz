package com.tappitz.tappitz.background;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.FutureTarget;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.FutureUpload;
import com.tappitz.tappitz.model.FutureVote;
import com.tappitz.tappitz.model.FutureWorkList;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.model.SentPicture;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.model.CreatePhoto;
import com.tappitz.tappitz.rest.model.VoteInbox;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ModelCache;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;

/**
 * Created by joaosampaio on 24-02-2016.
 */
public class BackgroundService extends Service {

    private boolean isStarted;
    private BackgroundThread run;
    private Context ctx;
    private FutureWorkList work;
    private ScreenSlidePagerActivity activity;
    private int consecutiveErrors;
    private final IBinder mBinder = new LocalBinder();
    private int width, height;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public BackgroundService getService(){
            return BackgroundService.this;
        }
    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(ScreenSlidePagerActivity activity){
        this.activity = activity;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        width = sp.getInt(Global.SCREEN_WIDTH, 0);
        height = sp.getInt(Global.SCREEN_HEIGHT, 0);

        isStarted = false;
        ctx = AppController.getInstance().getApplicationContext();
        consecutiveErrors = 0;
        run = new BackgroundThread(false);
//        run = new Runnable() {
//            @Override
//            public void run() {
//
//                }
//            }
//        };
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("servico", "onStartCommand");
        //check internet
        String origin = "";
        if(intent != null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                origin = extras.getString("origin", "");
            }
        }


        if (!isStarted) {
            // loggin
            isStarted = true;
            if(origin.equals("receiver"))
                run.setFromReceiver(true);

            new Thread(run).start();
//                    handler.post(run);
        }



        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isStarted = false;
    }


    public static boolean isWifiAvailable(){

        ConnectivityManager cm =
                (ConnectivityManager) AppController.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

//        ConnectivityManager connManager = (ConnectivityManager) AppController.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        return mWifi.isConnected();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    private void saveFutureWork(FutureWorkList work){
        new ModelCache<FutureWorkList>().saveModel(ctx, work, Global.OFFLINE_WORK);
    }


    public void updateInbox(int pictureId, FutureVote vote){

        List<ReceivedPhoto> tmp = new ModelCache<List<ReceivedPhoto>>().loadModel(ctx, new TypeToken<List<ReceivedPhoto>>() {
        }.getType(), Global.OFFLINE_INBOX);

        if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof ReceivedPhoto) {

            ReceivedPhoto old = ReceivedPhoto.getPhotoWithId(tmp, pictureId);
            if (old != null){

                old.setVote(vote.getVote());
                old.setComment(vote.getComment());
                old.setHasVoted(true);
                old.setIsVoteTemporary(false);
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                old.setVotedDate(now);
                new ModelCache<List<ReceivedPhoto>>().saveModel(ctx, tmp, Global.OFFLINE_INBOX);
                if(activity != null && activity.getReloadInboxListener() != null){
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(ctx.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            activity.getReloadInboxListener().updateAfterVote();
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                    //activity.getReloadOutbox().updateFinalOutbox(old);

                }
            }
        }
    }



    private SentPicture getSentWithId(int id, List<SentPicture> photos){
        for(SentPicture p: photos){
            if(p.getId() == id)
                return p;
        }
        return null;
    }

    private void updateOutbox(int newId, FutureUpload upload){

        final String url = Global.ENDPOINT + "/pictures/"+newId;
        Log.d("servico", "preloading " + url + " width:" + width + " height:" + height);

        Time time;
        time = new Time();time.setToNow();
        Log.d("TIME TEST 1","loading image"+ Long.toString(time.toMillis(false)));


        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String sessionId = sp.getString("sessionId", "");
        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Content-type", "application/json;charset=UTF-8")
                .addHeader("Accept", "application/json")
                .addHeader("Session-Id", sessionId)
                .build());

        FutureTarget<File> future = Glide.with(AppController.getInstance().getApplicationContext())
                .load(glideUrl)
                .downloadOnly(width, height);
        try {
            File cacheFile = future.get();

            time = new Time();time.setToNow();
            Log.d("TIME AsyncTask myBitmap", "cacheFile.length():"+cacheFile.length()+"||depois do get:" +Long.toString(time.toMillis(false)));
            time = new Time();time.setToNow();
            Log.d("TIME AsyncTask3", Long.toString(time.toMillis(false)));
        } catch (InterruptedException e) {
            Log.d("TIME", "ex:" + e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d("TIME", "ex:" + e.getMessage());
            e.printStackTrace();
        }

        Log.d("servico", "updateOutbox");
        List<SentPicture> tmp = new ModelCache<List<SentPicture>>().loadModel(ctx, new TypeToken<List<SentPicture>>() {
        }.getType(), Global.OFFLINE_OUTBOX);

        if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof SentPicture) {
            final SentPicture old = getSentWithId(upload.getTmpId(), tmp);
            if (old != null){
                old.setIsTemporary(false);
                old.setId(newId);
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                old.setCreatedDate(now);
                new ModelCache<List<SentPicture>>().saveModel(ctx, tmp, Global.OFFLINE_OUTBOX);
                if(activity != null && activity.getReloadOutbox() != null){
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(ctx.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            activity.getReloadOutbox().refreshOfflineOutbox();
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                        //activity.getReloadOutbox().updateFinalOutbox(old);

                }
            }
        }
    }

    private boolean checkSession(){
        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String sessionId = sp.getString("sessionId", "");


        if(Global.VERSION_V2) {
            RestClientV2.setSessionId(sessionId);
            Call<JsonElement> call = RestClientV2.getService().isLogin();
            try {
                JsonElement json = call.execute().body();
                if (json != null) {
                    JsonObject obj = json.getAsJsonObject();
                    boolean status = obj.get("status").getAsBoolean();
                    if (status) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
            JsonElement json = RestClient.getService().isLoginBlock();
                if (json != null) {
                    JsonObject obj = json.getAsJsonObject();
                    boolean status = obj.get("status").getAsBoolean();
                    if (status) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

//    private boolean loginClient(){
//        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
//        final String email = sp.getString(Global.KEY_USER, "");
//        final String password  = sp.getString(Global.KEY_PASS, "");
//        if(email.length() > 0 && password.length() > 0){
//            Call<JsonElement> call = RestClientV2.getService().login(new UserLogin(email, password));
//            try {
//                JsonElement json = call.execute().body();
//                if (json != null) {
//                    JsonObject obj = json.getAsJsonObject();
//                    boolean status = obj.get("status").getAsBoolean();
//                    if (status) {
//                        String sessionId = json.getAsJsonObject().get("sessionId").getAsString();
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putString("sessionId", sessionId);
//                        editor.commit();
//                        RestClientV2.setSessionId(sessionId);
//                        return true;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }

    private void doWork(){
        List<FutureVote> votes;
        List<FutureUpload> uploads;
        Call<JsonElement> call;
        JsonElement json;
        work = new ModelCache<FutureWorkList>().loadModel(ctx,new TypeToken<FutureWorkList>(){}.getType(), Global.OFFLINE_WORK);
        if(work == null)
            work = new FutureWorkList();

        votes = work.getVotes();
        uploads = work.getUploads();
        if(votes.size() > 0){
            FutureVote vote = votes.get(0);
            if(Global.VERSION_V2) {
                call = RestClientV2.getService().sendVotePicture(new VoteInbox(vote.getVote(), vote.getPictureId(), vote.getComment()));
                try {
                    json = call.execute().body();
                    if (json != null) {
                        JsonObject obj = json.getAsJsonObject();
                        boolean status = obj.get("status").getAsBoolean();
                        if (status) {
                            updateInbox(vote.getPictureId(), vote);
                        } else {
                            //o servidor diz que nao pode aceitar vamos assumir que no caso do voto ele ja foi aceite e apagamos
                        }
                        //volto a ir buscar o FutureWorkList porque pode ter passado vários segundos desde o inicio e pode haver mais trabalho
                        work = new ModelCache<FutureWorkList>().loadModel(ctx, new TypeToken<FutureWorkList>() {
                        }.getType(), Global.OFFLINE_WORK);
                        work.removeVote(vote.getPictureId());
                        saveFutureWork(work);
                        consecutiveErrors = 0;
                    }

                } catch (IOException e) {
                    //houve um problema a contactar o servidor
                    consecutiveErrors++;
                    e.printStackTrace();
                    //paro o serviço houve demasiados problemas
                    if (consecutiveErrors >= 2) {
                        stopSelf();
                        return;
                    }
                }
            }else{

                try {
                    json = RestClient.getService().sendVotePictureBlock(new VoteInbox(vote.getVote(), vote.getPictureId(), vote.getComment()));
                    if (json != null) {
                        JsonObject obj = json.getAsJsonObject();
                        boolean status = obj.get("status").getAsBoolean();
                        if (status) {
                            updateInbox(vote.getPictureId(), vote);
                        } else {
                            //o servidor diz que nao pode aceitar vamos assumir que no caso do voto ele ja foi aceite e apagamos
                        }
                        //volto a ir buscar o FutureWorkList porque pode ter passado vários segundos desde o inicio e pode haver mais trabalho
                        work = new ModelCache<FutureWorkList>().loadModel(ctx, new TypeToken<FutureWorkList>() {
                        }.getType(), Global.OFFLINE_WORK);
                        work.removeVote(vote.getPictureId());
                        saveFutureWork(work);
                        consecutiveErrors = 0;
                    }

                } catch (Exception e) {
                    //houve um problema a contactar o servidor
                    consecutiveErrors++;
                    e.printStackTrace();
                    //paro o serviço houve demasiados problemas
                    if (consecutiveErrors >= 2) {
                        stopSelf();
                        return;
                    }
                }
            }
        }

        if(uploads.size() > 0){
            FutureUpload upload = uploads.get(0);
            String imageBase64 = upload.getBase64Image();
            if(imageBase64 == null){
                //já nao existe a imagem nao vamos poder envia-la
                work = new ModelCache<FutureWorkList>().loadModel(ctx,new TypeToken<FutureWorkList>(){}.getType(), Global.OFFLINE_WORK);
                work.removeUpload(upload.getTmpId());
                saveFutureWork(work);
                consecutiveErrors = 0;
            }else {
                Log.d("sendPhoto", "getComment: ->"+ upload.getComment());
                Gson gson = new Gson();
                Log.d("sendPhoto", "getFriendIds: ->"+ gson.toJson(upload.getFriendIds()));
                Log.d("sendPhoto", "isSendToFollowers: ->"+ upload.isSendToFollowers());
                call = RestClientV2.getService().sendphoto(new CreatePhoto(upload.getComment(), upload.getFriendIds(), imageBase64, upload.isSendToFollowers()));
                try {
                    json = call.execute().body();
                    final int oldId = upload.getTmpId();
                    if (json != null) {
                        JsonObject obj = json.getAsJsonObject();
                        boolean status = obj.get("status").getAsBoolean();
                        if (status) {
                            int newId = obj.get("id").getAsInt();

                            updateOutbox(newId, upload);

                        } else {
                            //pensar sobre o que acontece qd a imagem nao é enviada

                        }

                        //volto a ir buscar o FutureWorkList porque pode ter passado vários segundos desde o inicio e pode haver mais trabalho
                        work = new ModelCache<FutureWorkList>().loadModel(ctx, new TypeToken<FutureWorkList>() {
                        }.getType(), Global.OFFLINE_WORK);
                        work.removeUpload(oldId);
                        saveFutureWork(work);
                        consecutiveErrors = 0;
                    }

                } catch (IOException e) {
                    //houve um problema a contactar o servidor
                    consecutiveErrors++;
                    e.printStackTrace();
                    //paro o serviço houve demasiados problemas
                    if (consecutiveErrors >= 2) {
                        stopSelf();
                        return;
                    }
                }
            }
        }
        Log.d("worklist", "end do work:"+uploads.size() );
    }


    public static void addPhotoUploadWork(FutureUpload upload){
        FutureWorkList work = new ModelCache<FutureWorkList>().loadModel(AppController.getAppContext(),new TypeToken<FutureWorkList>(){}.getType(), Global.OFFLINE_WORK);
        if(work == null)
            work = new FutureWorkList();
        work.addUploadWork(upload);
        new ModelCache<FutureWorkList>().saveModel(AppController.getAppContext(), work, Global.OFFLINE_WORK);
    }

    public static void addVoteWork(FutureVote vote){
        FutureWorkList work = new ModelCache<FutureWorkList>().loadModel(AppController.getAppContext(),new TypeToken<FutureWorkList>(){}.getType(), Global.OFFLINE_WORK);
        if(work == null)
            work = new FutureWorkList();
        work.addVoteWork(vote);
        new ModelCache<FutureWorkList>().saveModel(AppController.getAppContext(), work, Global.OFFLINE_WORK);
    }

    //we check if there are some pictures that are temporary but are not in the work list, we delete them
    //can happen if there are exceptions are the picture was sent but not updated
    private void cleanSentPictures(){
        work = new ModelCache<FutureWorkList>().loadModel(ctx, new TypeToken<FutureWorkList>() {
        }.getType(), Global.OFFLINE_WORK);
        if(work == null)
            work = new FutureWorkList();
        List<SentPicture> fullList = new ModelCache<List<SentPicture>>().loadModel(ctx, new TypeToken<List<SentPicture>>() {
        }.getType(), Global.OFFLINE_OUTBOX);
        if(fullList == null)
            fullList = new ArrayList<>();
        int beforeSize = fullList.size();
        List<SentPicture> changedList = work.removeFailed(fullList);
        if(changedList.size() != beforeSize){
            Log.d("servico", "changedList.size() != fullList.size()");
            new ModelCache<List<SentPicture>>().saveModel(ctx, changedList, Global.OFFLINE_OUTBOX);
            //refresh do adapter falta!
            if(activity != null && activity.getReloadOutbox() != null){
                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(ctx.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        activity.getReloadOutbox().refreshOfflineOutbox();
                    } // This is your code
                };
                mainHandler.post(myRunnable);
                //activity.getReloadOutbox().updateFinalOutbox(old);
            }
        }
    }



    class BackgroundThread implements Runnable{

        private boolean fromReceiver;

        public BackgroundThread(boolean fromReceiver) {
            this.fromReceiver = fromReceiver;
        }

        public void setFromReceiver(boolean fromReceiver) {
            this.fromReceiver = fromReceiver;
        }

        @Override
        public void run() {
            Log.d("servico", "Inicio do RUN");
            boolean go = true;

            if(fromReceiver)
                try {
                    Log.d("servico", "Is from receiver will sleep for 20s");
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("servico", "antes InterruptedException");
                }


            int consecutiveErrors = 0;

            boolean isLoggedIn = false;
            SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
            while(go) {
                Log.d("servico", "Loop do run");
                //nao ha wifi parar
                if (!isWifiAvailable()) {
                    Log.d("servico", "no wifi ...");

                    if(activity != null){
                        Log.d("servico", "no wifi but app is running will sleep for 20s and try again");
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }else{
                        go = false;
                        isStarted = false;
                        stopSelf();
                        return;
                    }
                }
                isLoggedIn = checkSession();

                Log.d("servico", "Tenho sessao:" + isLoggedIn);
//                if (!isLoggedIn) {
//                    isLoggedIn = loginClient();
//                }

                Log.d("servico", "Tenho sessao2:" + isLoggedIn);
                if (!isLoggedIn) {
                    go = false;
                    isStarted = false;
                    stopSelf();
                    return;
                }

//                Glide.get(ctx).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RestClientV2.getOk()));


                doWork();

                //if no more work we stop
                if (!work.hasWork()) {
                    Log.d("servico", "no more work.................");
                    cleanSentPictures();

                    go = false;
                    isStarted = false;
                    stopSelf();
                    return;
                }
            }
        }
    }

}
