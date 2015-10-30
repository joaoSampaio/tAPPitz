package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.rest.model.UserLogin;
import com.tappitz.tappitz.rest.RestClient;

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
