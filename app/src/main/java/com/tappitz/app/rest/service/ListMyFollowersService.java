package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.model.Contact;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.GenericContact;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class ListMyFollowersService extends ServerCommunicationService {

    private CallbackMultiple callback;
    public ListMyFollowersService(CallbackMultiple callback){
        this.callback = callback;
    }

    @Override
    public void execute() {

        if (Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().listMyFollowers();
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            Gson gson = new Gson();
                            JsonObject obj = json.getAsJsonObject();
                            boolean status = obj.get("status").getAsBoolean();
                            if (status) {
                                List<GenericContact> genericContacts = gson.fromJson(obj.get("followers"), new TypeToken<List<GenericContact>>() {
                                }.getType());
                                List<Contact> followers = new ArrayList<Contact>();
                                Contact myFollower;
                                for (GenericContact c : genericContacts) {
                                    myFollower = new Contact(c.getName(), c.getUsername(), c.getEmail(), c.getId());
                                    myFollower.setIsFollower(true);
                                    followers.add(myFollower);
                                }

                                callback.success(followers);
                            } else {
                                Log.d("myapp", "deu erro");
                                callback.failed(obj.get("error").getAsString());
                            }
                        }
                         else {
                            callback.failed("");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        } else {
            RestClient.getService().listMyFollowers(new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {

                    Gson gson = new Gson();
                    JsonObject obj = json.getAsJsonObject();
                    boolean status = obj.get("status").getAsBoolean();
                    if (status) {
                        List<GenericContact> genericContacts = gson.fromJson(obj.get("followers"), new TypeToken<List<GenericContact>>() {
                        }.getType());
                        List<Contact> followers = new ArrayList<Contact>();
                        Contact myFollower;
                        for (GenericContact c : genericContacts) {
                            myFollower = new Contact(c.getName(), c.getUsername(), c.getEmail(), c.getId());
                            myFollower.setIsFollower(true);
                            followers.add(myFollower);
                        }

                        callback.success(followers);
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
