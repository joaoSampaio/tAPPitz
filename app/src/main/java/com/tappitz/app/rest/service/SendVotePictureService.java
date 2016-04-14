package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.Global;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.VoteInbox;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class SendVotePictureService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private int id;
    private String myComment;
    private int choice;
    public SendVotePictureService(String myComment, int id, int choice, CallbackMultiple callback){
        this.callback = callback;
        this.choice = choice;
        this.id = id;
        this.myComment = myComment;
    }

    @Override
    public void execute() {

        if (Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().sendVotePicture(new VoteInbox(choice, id, myComment));
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            JsonObject obj = json.getAsJsonObject();
                            boolean status = obj.get("status").getAsBoolean();
                            if (status) {
                                callback.success(true);
                            } else {
                                callback.failed(obj != null ? obj.get("error").getAsString() : "error");
                            }
                        } else {
                            callback.failed("problem");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        } else {
            RestClient.getService().sendVotePicture(new VoteInbox(choice, id, myComment), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {

                    JsonObject obj = json.getAsJsonObject();
                    Log.d("myapp", "obj->" + obj.toString());
                    boolean status = obj.get("status").getAsBoolean();
                    if (status) {
                        callback.success(true);
                    } else {
                        Log.d("myapp", "deu erro");
                        callback.failed(obj != null ? obj.get("error").getAsString() : "error");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("myapp", "**error*hhhh***" + error.toString());
                    callback.failed("network problem");
                }
            });

        }
    }
}
