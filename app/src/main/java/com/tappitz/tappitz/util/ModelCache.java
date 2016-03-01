package com.tappitz.tappitz.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.model.PhotoOutbox;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ModelCache<T> {

    public void saveModel(Context cxt,T list, String TAG){
        String json = new Gson().toJson(list);
        SharedPreferences sp = cxt.getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TAG, json);
        editor.commit();
    }

    public T loadModel(Context cxt,Type type, String TAG){
        String contacts = "";
        try {
            SharedPreferences sp = cxt.getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
            contacts = sp.getString(TAG, "");
        }catch (Exception e){
            Log.d("myapp", "error:" + e.getMessage());
        }
        if (contacts.equals("")) {
            return null;
        } else {
            T contactsList = new Gson().fromJson(contacts, type);
            return contactsList;
        }

    }

}
