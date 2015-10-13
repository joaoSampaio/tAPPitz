package com.tappitz.tappitz.server.commands;


import android.util.Log;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.model.UserRegister;
import com.tappitz.tappitz.server.CallbackFromService;
import com.tappitz.tappitz.server.RestClient;
import com.tappitz.tappitz.server.ServerCommunicationService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterService implements ServerCommunicationService {

    private CallbackFromService callback;
    private UserRegister user;
    public RegisterService(UserRegister user, CallbackFromService callback){
        this.user = user;
        this.callback = callback;
    }

    @Override
    public void execute() {

        RestClient.getService().register(user, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                callback.success(jsonElement);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed(error);
            }
        });


    }
}
