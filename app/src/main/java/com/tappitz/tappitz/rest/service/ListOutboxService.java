package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.PhotoOutbox;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListOutboxService implements ServerCommunicationService {

    private CallbackMultiple callback;
    public ListOutboxService(CallbackMultiple callback){
        this.callback = callback;
    }

    @Override
    public void execute() {

        RestClient.getService().requestOutbox(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement  json, Response response2) {

                Gson gson = new Gson();
                JsonObject obj = json.getAsJsonObject();
                Log.d("myapp", "obj->" + obj.toString());

                List<PhotoOutbox> outbox = gson.fromJson(obj.get("pictures"), new TypeToken<List<PhotoOutbox>>() {
                }.getType());
                //Log.d("myapp", "genericContacts: " + outbox.size());
                if (outbox == null)
                    outbox = new ArrayList<PhotoOutbox>();
                callback.success(outbox);


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed(error);
            }
        });
    }
}
