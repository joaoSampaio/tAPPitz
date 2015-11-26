package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.ContactSendId;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DeleteContactService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private ContactSendId id;
    public DeleteContactService(ContactSendId id, CallbackMultiple callback){
        this.callback = callback;
        this.id = id;
    }

    @Override
    public void execute() {
        RestClient.getService().deleteContact(id, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                JsonObject obj = json.getAsJsonObject();
                boolean status = obj.get("status").getAsBoolean();
                callback.success(status);
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
