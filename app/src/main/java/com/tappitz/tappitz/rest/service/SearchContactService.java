package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.model.ContactSearchResult;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchContactService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private ContactSendId search;
    public SearchContactService(ContactSendId search, CallbackMultiple callback){
        this.callback = callback;
        this.search = search;
    }

    @Override
    public void execute() {
        //RestClient.getService().searchContact(search, new Callback<JsonElement>() {
        RestClient.getService().searchContact(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                Gson gson = new Gson();
                JsonObject obj = json.getAsJsonObject();
                Log.d("myapp", "obj->" + obj.toString());
                boolean status = obj.get("status").getAsBoolean();
                Log.d("myapp", "status->" + status);
                if (status) {
                    if(obj.get("data").toString().equals("{}")) {
                        callback.success(null);
                        return;
                    }
                    ContactSearchResult contactSearch = gson.fromJson(obj.get("data"), ContactSearchResult.class);
                    if(contactSearch == null || contactSearch.getEmail() == null) {
                        callback.success(null);
                        return;
                    }

                    Contact contact = new Contact(contactSearch.getName(), contactSearch.getEmail(), false, contactSearch.isInvited());
                    callback.success(contact);
                } else {
                    Log.d("myapp", "deu erro");
                    callback.failed(null);
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
