package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.ContactSendId;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BlockContactService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private ContactSendId id;
    public BlockContactService(ContactSendId id, CallbackMultiple callback){
        this.callback = callback;
        this.id = id;
    }

    @Override
    public void execute() {
        RestClient.getService().blockContact(id, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                JsonObject obj = json.getAsJsonObject();
                Log.d("myapp", "obj->" + obj.toString());
                boolean status = obj.get("status").getAsBoolean();

                Log.d("myapp", "status->" + status);
                callback.success(status);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed(error);
            }
        });


    }
}
