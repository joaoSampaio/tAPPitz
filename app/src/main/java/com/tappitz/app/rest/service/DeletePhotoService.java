package com.tappitz.app.rest.service;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.Global;
import com.tappitz.app.rest.RestClientV2;

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
