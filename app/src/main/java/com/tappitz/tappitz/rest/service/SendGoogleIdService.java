package com.tappitz.tappitz.rest.service;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.CreatePhoto;
import com.tappitz.tappitz.rest.model.GoogleId;

import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SendGoogleIdService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private Context context;


    public SendGoogleIdService(Context context, CallbackMultiple callback) {
        this.callback = callback;
        this.context = context;

    }

    @Override
    public void execute() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

                    String deviceToken = null;
                    try {
                        deviceToken = gcm.register(Global.PROJECT_ID);

                        if(deviceToken == null) {
                            callback.failed("no token");
                            return null;
                        }

                        RestClient.getService().sendGoogleId(new GoogleId(deviceToken), new Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement json, Response response2) {

                                JsonObject obj = json.getAsJsonObject();
                                Log.d("myapp", "**obj****->" + obj);
                                boolean status = obj.get("status").getAsBoolean();
                                if (status)
                                    callback.success(status);
                                else
                                    callback.failed(obj.get("error").getAsString());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("myapp", "**error****" + error.toString());
                                callback.failed(error.toString());
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("GCM", "Device token : " + deviceToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();

//
//
//
//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
//
//        String deviceToken = null;
//        try {
//            deviceToken = gcm.register(Global.PROJECT_ID);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("myapp", "deviceToken == null" + (deviceToken == null));
//        if(deviceToken == null) {
//            callback.failed("no token");
//            return;
//        }
//
//        RestClient.getService().sendGoogleId(new GoogleId(deviceToken), new Callback<JsonElement>() {
//            @Override
//            public void success(JsonElement json, Response response2) {
//
//                JsonObject obj = json.getAsJsonObject();
//                Log.d("myapp", "**obj****->" + obj);
//                boolean status = obj.get("status").getAsBoolean();
//                if (status)
//                    callback.success(status);
//                else
//                    callback.failed(obj.get("error").getAsString());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.d("myapp", "**error****" + error.toString());
//                callback.failed(error.toString());
//            }
//        });


    }
}
