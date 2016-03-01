package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.model.UserLogin;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class LoginService extends ServerCommunicationService {

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
        if(Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().login(login);
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        JsonObject obj = json.getAsJsonObject();
                        boolean status = obj.get("status").getAsBoolean();
                        String sessionId = "";
                        if (status) {
                            try {
                                sessionId = json.getAsJsonObject().get("sessionId").getAsString();
                            } catch (Exception e) {
                                e.printStackTrace();
                                callback.failed("network problem");
                                return;
                            }

                            callback.success(sessionId);
                        } else {
                            Log.d("myapp", "deu erro");
                            callback.failed(obj.get("error").getAsString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });}
        else {

            RestClient.getService().login(login, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    JsonObject obj = jsonElement.getAsJsonObject();
                    boolean status = obj.get("status").getAsBoolean();
                    String sessionId = "";
                    if (status) {
                        try {
                            sessionId = jsonElement.getAsJsonObject().get("sessionId").getAsString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.failed("network problem");
                            return;
                        }

                        callback.success(sessionId);
                    } else {
                        Log.d("myapp", "deu erro");
                        callback.failed(obj.get("error").getAsString());
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("myapp", "**error****" + error.toString());
                    callback.failed("network problem");
                }
            });
        }

    }
}
