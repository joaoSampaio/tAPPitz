package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.InBoxPagerAdapter;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;


public class InBoxFragment extends Fragment {

    View rootView;
    private InBoxPagerAdapter adapter;
    private List<PhotoInbox> photos;
    public InBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_in_box, container, false);

        Log.d("myapp2", "**--new InBoxFragment:");
//        rootView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
//                String sessionid = sp.getString("session_id", "");
//                RestClient.getService().isLogin( new Callback<JsonElement>() {
//                    @Override
//                    public void success(JsonElement jsonElement, Response response) {
//
//                        Log.d("myapp", "***get(status)***" + jsonElement.getAsJsonObject().get("status"));
//                        String status = jsonElement.getAsJsonObject().get("status").toString();
//                        if(getActivity() != null)
//                            Toast.makeText(getActivity(), "resposta: " + status, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        if(getActivity() != null)
//                            Toast.makeText(getActivity(), "erro: ", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//            }
//        });




        photos = new ArrayList<>();
        adapter = new InBoxPagerAdapter(getChildFragmentManager(), photos);
        Log.d("myapp2", "**--new OutBoxFragment:");
        VerticalViewPager viewPager = (VerticalViewPager) rootView.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(adapter);

        new ListInboxService(new CallbackMultiple<List<PhotoInbox>>() {
            @Override
            public void success(List<PhotoInbox> response) {
                photos.clear();
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


    private void OnDoneLoading(){

//String url, String id, String text,String date, boolean hasVoted,  String myComment, String senderName
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/1.jpg", "11111112", "Gostas deste edificio?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/2.jpg", "11111112", "Curtes?", "15/10/2015 - 12:25", true, "Muito bonito!", "João Sampaio", 2));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/3.jpg", "11111112", "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/4.jpg", "11111112", "bla bla bla e texto e mais texto!!!!", "15/10/2015 - 12:35", false, "", "João Sampaio", 1));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/5.jpg", "11111112", "Gostas deste edificio?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));


               //notifico o adapter para atualizar a lista
        adapter.notifyDataSetChanged();
    }


}
