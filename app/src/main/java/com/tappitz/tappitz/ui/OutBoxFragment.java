package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.OutBoxPagerAdapter;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListOutboxService;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class OutBoxFragment extends Fragment {


    View rootView;
    private OutBoxPagerAdapter adapter;
    private List<PhotoOutbox> photos;
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
        new ListOutboxService(new CallbackMultiple<List<PhotoOutbox>>() {
            @Override
            public void success(List<PhotoOutbox> response) {
                photos.addAll(response);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failed(Object error) {
                OnDoneLoading();
            }
        }).execute();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void OnDoneLoading(){


//        photos.add(new PhotoOutbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/1.jpg", 1, "Gostas deste edificio?"));
//        photos.add(new PhotoOutbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/2.jpg", 2, "Parece mesmo alto?"));
//        photos.add(new PhotoOutbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/3.jpg", 4, "Curtes"));
//        photos.add(new PhotoOutbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/4.jpg", 5, "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));
//
//        photos.add(new PhotoOutbox("http://cdn.bgr.com/2014/07/android-blue.jpg", 6, "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));
//        photos.add(new PhotoOutbox("http://cdn.gsmarena.com/vv/newsimg/14/06/androidone/gsmarena_001.jpg", 8, "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));
//        photos.add(new PhotoOutbox("https://gigaom.com/wp-content/uploads/sites/1/2011/01/android-vs-ios.jpeg?quality=80&strip=all", 9, "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)"));

        //notifico o adapter para atualizar a lista
        adapter.notifyDataSetChanged();
    }




}
