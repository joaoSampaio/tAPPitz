package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.PhotoInbox;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListInboxService implements ServerCommunicationService {

    private CallbackMultiple callback;
    public ListInboxService(CallbackMultiple callback){
        this.callback = callback;
    }

    @Override
    public void execute() {

        RestClient.getService().requestInbox(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement  json, Response response2) {

                Log.d("myapp", "obj json != null" + (json != null));
                if(json != null) {
                    Gson gson = new Gson();
                    JsonObject obj = json.getAsJsonObject();
                    Log.d("myapp", "obj->" + obj.toString());
                    boolean status = obj.get("status").getAsBoolean();
                    if (status) {
                        List<PhotoInbox> inbox = gson.fromJson(obj.get("data"), new TypeToken<List<PhotoInbox>>() {
                        }.getType());
                               callback.success(inbox);
                    } else {
                        Log.d("myapp", "deu erro");
                        callback.failed(obj.get("error").getAsString());
                    }
                }else {
                    callback.success(new ArrayList<PhotoInbox>());
                }



            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed("network problem");
            }
        });


    }
}
