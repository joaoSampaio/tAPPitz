package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class CheckLoggedStateService extends ServerCommunicationService {

    private CallbackMultiple callback;
    public CheckLoggedStateService(CallbackMultiple callback){
        this.callback = callback;
    }

    @Override
    public void execute() {

        if(Global.VERSION_V2) {
        Call<JsonElement> call = RestClientV2.getService().isLogin();
        call.enqueue(new retrofit2.Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if (response.isSuccess()){
                    JsonElement json = response.body();
                    JsonObject obj = json.getAsJsonObject();
                    boolean status = obj.get("status").getAsBoolean();
                    Log.d("myapp", "status->" + status);
                    if (status) {
                        callback.success(null);
                    } else {
                        Log.d("myapp", "deu erro");
                        callback.failed(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.failed(null);
            }
        });}
        else {

        RestClient.getService().isLogin(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                JsonObject obj = json.getAsJsonObject();
                boolean status = obj.get("status").getAsBoolean();
                Log.d("myapp", "status->" + status);
                if (status) {
                    callback.success(null);
                } else {
                    Log.d("myapp", "deu erro");
                    callback.failed(null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("myapp", "**error****" + error.toString());
                callback.failed(error.toString());
            }
        });
        }

    }
}
