package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.model.CreatePhoto;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class DeletePhotoService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private int id;

    public DeletePhotoService( int id, CallbackMultiple callback) {
        this.callback = callback;
        this.id = id;

    }

    @Override
    public void execute() {
        if(Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().deletePhoto(id);
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            JsonObject obj = json.getAsJsonObject();
                            boolean status = obj.get("status").getAsBoolean();
                            if (status)
                                callback.success("");
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
        }
    }
}
