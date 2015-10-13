package com.tappitz.tappitz.server.commands;


import android.util.Log;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.model.UserLogin;
import com.tappitz.tappitz.server.CallbackFromService;
import com.tappitz.tappitz.server.RestClient;
import com.tappitz.tappitz.server.ServerCommunicationService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginService implements ServerCommunicationService {

    private CallbackFromService callback;
    private String email, password;
    public LoginService(String email, String password, CallbackFromService callback){
        this.password = password;
        this.email = email;
        this.callback = callback;
    }

    @Override
    public void execute() {

        UserLogin login = new UserLogin(email, password);
        RestClient.getService().login(login, new Callback<JsonElement>() {
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
