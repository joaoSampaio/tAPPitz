package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.model.Vote;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class ListVotesService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private int pictureId;
    public ListVotesService(int pictureId,CallbackMultiple callback){
        this.callback = callback;
        this.pictureId = pictureId;
    }

    @Override
    public void execute() {

        if (Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().getOutboxComments(pictureId);
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            Gson gson = new Gson();
                            JsonObject obj = json.getAsJsonObject();

                            List<Vote> votes = gson.fromJson(obj.get("votes"), new TypeToken<List<Vote>>() {
                            }.getType());

                            if (votes == null)
                                votes = new ArrayList<Vote>();
                            List<Comment> comments = new ArrayList<Comment>();
                            Comment c;
                            for (Vote v : votes) {
                                c = v.convertToComment();
                                if (c != null)
                                    comments.add(c);
                            }

                            callback.success(comments);
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
            RestClient.getService().getOutboxComments(pictureId, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {

                    Gson gson = new Gson();
                    JsonObject obj = json.getAsJsonObject();
                    Log.d("myapp", "******8888****obj->" + obj.toString());

                    List<Vote> votes = gson.fromJson(obj.get("votes"), new TypeToken<List<Vote>>() {
                    }.getType());
                    //Log.d("myapp", "genericContacts: " + outbox.size());

                    if (votes == null)
                        votes = new ArrayList<Vote>();
                    List<Comment> comments = new ArrayList<Comment>();
                    Comment c;
                    for (Vote v : votes) {
                        c = v.convertToComment();
                        if (c != null)
                            comments.add(c);
                    }


                    callback.success(comments);


                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("myapp", "**error*votes***" + error.toString());
                    callback.failed(error.toString());
                }
            });
        }
    }
}
