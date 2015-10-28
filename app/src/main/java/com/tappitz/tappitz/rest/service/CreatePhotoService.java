package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient_Sigma;
import com.tappitz.tappitz.rest.model.CreatePhoto;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreatePhotoService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private String comment;
    private String picture;
    private List<String> contacts;

    public CreatePhotoService(String comment, List<String> contacts, String picture, CallbackMultiple callback) {
        this.callback = callback;
        this.comment = comment;
        this.contacts = contacts;
        this.picture = picture;
    }

    @Override
    public void execute() {
        RestClient_Sigma.getService().sendphoto(new CreatePhoto(comment, contacts, picture), new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                Gson gson = new Gson();
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
