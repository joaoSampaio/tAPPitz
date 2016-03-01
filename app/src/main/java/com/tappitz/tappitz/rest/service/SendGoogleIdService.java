package com.tappitz.tappitz.rest.service;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.model.CreatePhoto;
import com.tappitz.tappitz.rest.model.GoogleId;
import com.tappitz.tappitz.rest.model.Vote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class SendGoogleIdService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String deviceToken;


    public SendGoogleIdService(String deviceToken, CallbackMultiple callback) {
        this.callback = callback;
        this.deviceToken = deviceToken;

    }

    @Override
    public void execute() {

        if (Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().sendGoogleId(new GoogleId(deviceToken));
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            JsonObject obj = json.getAsJsonObject();
                            Log.d("myapp", "**obj****->" + obj);
                            boolean status = obj.get("status").getAsBoolean();
                            if (status)
                                callback.success(status);
                            else
                                callback.failed(obj.get("error").getAsString());
                        } else {
                            callback.failed("problem");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        } else {
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
        }
    }
}
