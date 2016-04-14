package com.tappitz.app.rest.service;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.FutureTarget;
import com.google.gson.JsonElement;
import com.tappitz.app.Global;
import com.tappitz.app.app.AppController;
import com.tappitz.app.camera.PhotoSave;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;

public class DownloadPhotoService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private int id;
    public DownloadPhotoService(int id, CallbackMultiple callback){
        this.callback = callback;
        this.id = id;
    }
    private Call<JsonElement> call;

    public void cancelRequest(){
        if(call != null){
            call.cancel();
        }
    }

    @Override
    public void execute() {
        new DownloadTask(id).execute();


    }

    public class DownloadTask extends AsyncTask<Void, Void, Intent> {

        int id;

        public DownloadTask(int id) {
            this.id = id;
        }

        @Override
        protected Intent doInBackground(Void... params) {

            SharedPreferences sp = AppController.getAppContext().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
            String sessionId = sp.getString("sessionId", "");
            int width = sp.getInt(Global.SCREEN_WIDTH, 0);
            int height = sp.getInt(Global.SCREEN_HEIGHT, 0);
            final String url = Global.ENDPOINT + "/pictures/"+id;
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
                FileInputStream fileInputStream=null;
                byte[] bFile = new byte[(int) cacheFile.length()];
                try {
                    //convert file into array of bytes
                    fileInputStream = new FileInputStream(cacheFile);
                    fileInputStream.read(bFile);
                    fileInputStream.close();

                    System.out.println("Done");
                }catch(Exception e){
                    e.printStackTrace();
                }
                cacheFile = null;
                File photo = PhotoSave.saveImageToFile(bFile);
                Intent intent = new Intent("", Uri.fromFile(photo));
                return intent;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            callback.success(intent);


        }
    }

}
