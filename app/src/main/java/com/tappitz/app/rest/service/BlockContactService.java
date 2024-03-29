package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.model.ContactSendId;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BlockContactService extends ServerCommunicationService {

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
                boolean status = obj.get("status").getAsBoolean();
                if(status)
                    callback.success(status);
                else
                    callback.failed(obj.get("error").getAsString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed("network problem");
            }
        });


    }
}
