package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.tappitz.app.Global;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.UserRegister;
import com.tappitz.app.rest.RestClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class RegisterService extends ServerCommunicationService {

    private CallbackFromService callback;
    private UserRegister user;
    public RegisterService(UserRegister user, CallbackFromService callback){
        this.user = user;
        this.callback = callback;
    }

    @Override
    public void execute() {

        if(Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().register(user);
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        callback.success(json);
                    } else {
                        callback.failed("Ups there was something wrong");
                    }
                }


                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }else {
            RestClient.getService().register(user, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    callback.success(jsonElement);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("myapp", "**error****" + error.toString());
                    callback.failed("error");
                }
            });

        }
    }
}
