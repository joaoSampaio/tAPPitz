package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.Global;
import com.tappitz.app.model.Contact;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.ContactSearchResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class SearchContactService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String search;
    public SearchContactService(String search, CallbackMultiple callback){
        this.callback = callback;
        this.search = search;
    }

    @Override
    public void execute() {

        if (Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().searchContact(search);
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
                                ContactSearchResult contactSearch = gson.fromJson(obj.get("data"), ContactSearchResult.class);
                                if (contactSearch == null || contactSearch.getEmail() == null) {
                                    callback.success(null);
                                    return;
                                }

                                Contact contact = new Contact(contactSearch.getName(), contactSearch.getUsername(), contactSearch.getEmail(), contactSearch.getId());
                                callback.success(contact);
                            } else {
                                Log.d("myapp", "deu erro");
                                callback.failed(obj.get("error").getAsString());
                            }
                        } else {
                            callback.failed("problem");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        } else {
            RestClient.getService().searchContact(search, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {

                    Gson gson = new Gson();
                    JsonObject obj = json.getAsJsonObject();
                    Log.d("myapp", "obj->" + obj.toString());
                    boolean status = obj.get("status").getAsBoolean();
                    Log.d("myapp", "status->" + status);
                    if (status) {
                        ContactSearchResult contactSearch = gson.fromJson(obj.get("data"), ContactSearchResult.class);
                        if (contactSearch == null || contactSearch.getEmail() == null) {
                            callback.success(null);
                            return;
                        }

                        Contact contact = new Contact(contactSearch.getName(), contactSearch.getUsername(), contactSearch.getEmail(), contactSearch.getId());
                        callback.success(contact);
                    } else {
                        Log.d("myapp", "deu erro");
                        callback.failed(obj.get("error").getAsString());
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
}
