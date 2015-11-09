package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.UserLogin;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private String email, password;
    public LoginService(String email, String password, CallbackMultiple callback){
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

                JsonObject obj = jsonElement.getAsJsonObject();
                boolean status = obj.get("status").getAsBoolean();
                String sessionId = "";
                if(status){
                    try {
                        sessionId = jsonElement.getAsJsonObject().get("sessionId").getAsString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.failed(null);
                        return;
                    }

                    callback.success(sessionId);
                }else{
                    Log.d("myapp", "deu erro");
                    String error = jsonElement.getAsJsonObject().get("error").toString();
                    callback.failed(error);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed(error);
            }
        });


    }
}
