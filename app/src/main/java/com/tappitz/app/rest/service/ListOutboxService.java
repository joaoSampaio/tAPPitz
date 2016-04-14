package com.tappitz.app.rest.service;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.model.SentPicture;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.PhotoOutbox;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class ListOutboxService extends ServerCommunicationService {

    private CallbackMultiple callback;
    public ListOutboxService(CallbackMultiple callback){
        this.callback = callback;
    }

    @Override
    public void execute() {

        if (Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().requestOutbox();
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            Gson gson = new Gson();
                            JsonObject obj = json.getAsJsonObject();
                            List<PhotoOutbox> outbox = gson.fromJson(obj.get("pictures"), new TypeToken<List<PhotoOutbox>>() {
                            }.getType());

                            List<SentPicture> result = new ArrayList<SentPicture>();
                            for (PhotoOutbox pic: outbox) {
                                result.add(new SentPicture(pic.getId(), pic.getText(), pic.getCreatedDate(), false));
                            }
                            callback.success(result);
                        } else {
                            callback.success(new ArrayList<SentPicture>());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        } else {
            RestClient.getService().requestOutbox(new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {

                    Gson gson = new Gson();
                    JsonObject obj = json.getAsJsonObject();
                    List<PhotoOutbox> outbox = gson.fromJson(obj.get("pictures"), new TypeToken<List<PhotoOutbox>>() {
                    }.getType());
                    if (outbox == null)
                        outbox = new ArrayList<PhotoOutbox>();
                    callback.success(outbox);


                }

                @Override
                public void failure(RetrofitError error) {
                    callback.failed(error.toString());
                }
            });
        }
    }
}
