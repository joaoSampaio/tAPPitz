package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.CreatePhoto;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreatePhotoService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private String comment;
    private String picture;
    private List<Integer> contacts;

    public CreatePhotoService(String comment, List<Integer> contacts, String picture, CallbackMultiple callback) {
        this.callback = callback;
        this.comment = comment;
        this.contacts = contacts;
        this.picture = picture;
    }

    @Override
    public void execute() {
        RestClient.getService().sendphoto(new CreatePhoto(comment, contacts, picture), new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                JsonObject obj = json.getAsJsonObject();
                boolean status = obj.get("status").getAsBoolean();
                if(status)
                    callback.success(obj.get("id").getAsInt());
                else
                    callback.failed(obj.get("error").getAsString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed("Ups there was something wrong, plz post more boobs.");
            }
        });


    }
}
