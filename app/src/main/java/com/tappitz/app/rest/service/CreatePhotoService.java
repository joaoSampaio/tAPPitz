package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.Global;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.CreatePhoto;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class CreatePhotoService extends ServerCommunicationService {

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
        if(Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().sendphoto(new CreatePhoto(comment, contacts, picture, true));
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            JsonObject obj = json.getAsJsonObject();
                            boolean status = obj.get("status").getAsBoolean();
                            if (status)
                                callback.success(obj.get("id").getAsInt());
                            else
                                callback.failed(obj.get("error").getAsString());
                        } else {
                            callback.failed("Ups there was something wrong");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }else {
            RestClient.getService().sendphoto(new CreatePhoto(comment, contacts, picture, true), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {

                    JsonObject obj = json.getAsJsonObject();
                    boolean status = obj.get("status").getAsBoolean();
                    if (status)
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
}
