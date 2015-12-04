package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.VoteInbox;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SendVotePictureService implements ServerCommunicationService {

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
                    callback.failed(obj != null? obj.get("error").getAsString() : "error");
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
