package com.tappitz.app.rest.service;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.Global;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.ContactSendId;

import retrofit2.Call;

public class OperationContactService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private ContactSendId contactOp;
    public OperationContactService(ContactSendId contactOp, CallbackMultiple callback){
        this.callback = callback;
        this.contactOp = contactOp;
    }

    @Override
    public void execute() {

        if(Global.VERSION_V2) {
            Call<JsonElement> call = RestClientV2.getService().operationContact(contactOp);
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                    if (response.isSuccess()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            Gson gson = new Gson();
                            JsonObject obj = json.getAsJsonObject();
                            boolean status = obj.get("status").getAsBoolean();
                            if (status)
                                callback.success(status);
                            else
                                callback.failed(obj.get("error").getAsString());
                        } else {
                            callback.failed("error");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }
    }
}
