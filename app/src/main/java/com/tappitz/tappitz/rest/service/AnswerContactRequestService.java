package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.AnswerContactRequest;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AnswerContactRequestService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private String email;
    private boolean answer;
    public AnswerContactRequestService(String email, boolean answer, CallbackMultiple callback){
        this.callback = callback;
        this.email = email;
        this.answer = answer;
    }

    @Override
    public void execute() {
        RestClient.getService().answerContactRequest(new AnswerContactRequest(email, answer), new Callback<JsonElement>() {
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
