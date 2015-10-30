package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.rest.RestClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class InBoxFragment extends Fragment {

    View rootView;
    public InBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_in_box, container, false);

        Log.d("myapp2", "**--new InBoxFragment:");
        rootView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                String sessionid = sp.getString("session_id", "");
                RestClient.getService().isLogin( new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {

                        Log.d("myapp", "***get(status)***" + jsonElement.getAsJsonObject().get("status"));
                        String status = jsonElement.getAsJsonObject().get("status").toString();
                        if(getActivity() != null)
                            Toast.makeText(getActivity(), "resposta: " + status, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(getActivity() != null)
                            Toast.makeText(getActivity(), "erro: ", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        return rootView;
    }



}
