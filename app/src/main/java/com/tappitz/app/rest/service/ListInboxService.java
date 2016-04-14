package com.tappitz.app.rest.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.PhotoInbox;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class ListInboxService extends ServerCommunicationService {

    private CallbackMultiple callback;
    public ListInboxService(CallbackMultiple callback){
        this.callback = callback;
    }
    private Call<JsonElement> call;

    public void cancelRequest(){
        if(call != null){
            call.cancel();
        }
    }

    @Override
    public void execute() {

        if(Global.VERSION_V2) {
            call = RestClientV2.getService().requestInbox();
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
                                List<PhotoInbox> inbox = gson.fromJson(obj.get("inbox"), new TypeToken<List<PhotoInbox>>() {
                                }.getType());


                                List<ReceivedPhoto> result = new ArrayList<ReceivedPhoto>();
                                ReceivedPhoto tmp;
                                for (PhotoInbox pic: inbox) {
                                    tmp = new ReceivedPhoto(pic.getPictureId(), pic.getPictureSentence(),
                                                       pic.getAuthorName(), pic.getSentDate(), pic.isHasVoted(),
                                                       pic.getVotedDate(), pic.getComment(), pic.getVote());
                                    tmp.setIsVoteTemporary(false);

                                    result.add(tmp);
                                }

                                callback.success(result);
                            } else {
                                Log.d("myapp", "deu erro");
                                callback.failed(obj.get("error").getAsString());
                            }
                        } else {
                            callback.success(new ArrayList<ReceivedPhoto>());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }else {


            RestClient.getService().requestInbox(new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response2) {


                    if (json != null) {
                        Gson gson = new Gson();
                        JsonObject obj = json.getAsJsonObject();
                        boolean status = obj.get("status").getAsBoolean();
                        if (status) {
                            List<PhotoInbox> inbox = gson.fromJson(obj.get("inbox"), new TypeToken<List<PhotoInbox>>() {
                            }.getType());
                            callback.success(inbox);
                        } else {
                            Log.d("myapp", "deu erro");
                            callback.failed(obj.get("error").getAsString());
                        }
                    } else {
                        callback.success(new ArrayList<PhotoInbox>());
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
