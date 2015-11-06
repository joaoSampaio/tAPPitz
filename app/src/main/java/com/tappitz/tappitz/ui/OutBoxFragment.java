package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.OutBoxPagerAdapter;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.photo_tAPPitz;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OutBoxFragment extends Fragment {


    View rootView;
    private OutBoxPagerAdapter adapter;
    private List<photo_tAPPitz> photos;
    public OutBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_out_box, container, false);

        //depois de pedir ao servidor um json com os dados crio uma lista de modelos e crio o pageview
        photos = new ArrayList<>();
        adapter = new OutBoxPagerAdapter(getChildFragmentManager(), photos);
        Log.d("myapp2", "**--new OutBoxFragment:");
        VerticalViewPager viewPager = (VerticalViewPager) rootView.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(adapter);
        RestClient.getService().getTapp(new Callback<List<photo_tAPPitz>>() {
            @Override
            public void success(List<photo_tAPPitz> photo_tAPPitzs, Response response) {

                try {
                    photos.addAll(photo_tAPPitzs);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    ///
                }
            }

            @Override
            public void failure(RetrofitError error) {
                OnDoneLoading();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void OnDoneLoading(){


        photos.add(new photo_tAPPitz("https://dl.dropboxusercontent.com/u/68830630/tAppitz/1.jpg", "11111112", "Gostas deste edificio?"));
        photos.add(new photo_tAPPitz("https://dl.dropboxusercontent.com/u/68830630/tAppitz/2.jpg", "11111113", "Parece mesmo alto?"));
        photos.add(new photo_tAPPitz("https://dl.dropboxusercontent.com/u/68830630/tAppitz/3.jpg", "11111114", "Curtes"));
        photos.add(new photo_tAPPitz("https://dl.dropboxusercontent.com/u/68830630/tAppitz/4.jpg", "11111115", "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));

        photos.add(new photo_tAPPitz("http://cdn.bgr.com/2014/07/android-blue.jpg", "11111115", "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));
        photos.add(new photo_tAPPitz("http://cdn.gsmarena.com/vv/newsimg/14/06/androidone/gsmarena_001.jpg", "11111115", "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));
        photos.add(new photo_tAPPitz("https://gigaom.com/wp-content/uploads/sites/1/2011/01/android-vs-ios.jpeg?quality=80&strip=all", "11111115", "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));

        //notifico o adapter para atualizar a lista
        adapter.notifyDataSetChanged();
    }




}
