package com.tappitz.tappitz.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.model.Vote;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListVotesService implements ServerCommunicationService {

    private CallbackMultiple callback;
    private int pictureId;
    public ListVotesService(int pictureId,CallbackMultiple callback){
        this.callback = callback;
        this.pictureId = pictureId;
    }

    @Override
    public void execute() {

        RestClient.getService().getOutboxComments(pictureId,new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response2) {

                Gson gson = new Gson();
                JsonObject obj = json.getAsJsonObject();
                Log.d("myapp", "******8888****obj->" + obj.toString());

                List<Vote> votes = gson.fromJson(obj.get("votes"), new TypeToken<List<Vote>>() {
                }.getType());
                //Log.d("myapp", "genericContacts: " + outbox.size());

                List<Comment> comments = new ArrayList<Comment>();
                Comment c;
                for (Vote v: votes) {
                    c = v.convertToComment();
                    if(c != null)
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
