package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.InBoxPagerAdapter;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;



public class InBoxFragment extends Fragment {

    View rootView;
    private InBoxPagerAdapter adapter;
    private List<PhotoInbox> photos;
    private VerticalViewPager viewPager;
    public InBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_in_box, container, false);

        Log.d("myapp2", "**--new InBoxFragment:");

        rootView.findViewById(R.id.action_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshInbox();
            }
        });

        photos = new ArrayList<>();
        adapter = new InBoxPagerAdapter(getChildFragmentManager(), photos);
        Log.d("myapp2", "**--new OutBoxFragment:");
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(adapter);


        refreshInbox();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((ScreenSlidePagerActivity)getActivity()).setReloadInboxListener(new OnNewPhotoReceived() {
            @Override
            public void refreshViewPager() {
                refreshInbox();
            }
        });
    }


    @Override
    public void onStop(){
        super.onStop();
        ((ScreenSlidePagerActivity)getActivity()).setReloadInboxListener(null);
    }


    private void refreshInbox(){
        Toast.makeText(getActivity(), "Refreshed!", Toast.LENGTH_SHORT).show();;
        new ListInboxService(new CallbackMultiple<List<PhotoInbox>>() {
            @Override
            public void success(List<PhotoInbox> response) {
                viewPager.setCurrentItem(0);
                photos.clear();
                photos.addAll(response);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void failed(Object error) {
                OnDoneLoading();
            }
        }).execute();
    }

    private void OnDoneLoading(){
        viewPager.setCurrentItem(0);
        photos.clear();
//String url, String id, String text,String date, boolean hasVoted,  String myComment, String senderName
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/1.jpg", 11111112, "Gostas deste edificio?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/2.jpg", 11111112, "Curtes?", "15/10/2015 - 12:25", true, "Muito bonito!", "João Sampaio", 2));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/3.jpg", 11111112, "Alguem conhce este livro? Devo ler isto? Penso que parece bom. Se alguem souber que comunique, pff :)", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/4.jpg", 11111112, "bla bla bla e texto e mais texto!!!!", "15/10/2015 - 12:35", false, "", "João Sampaio", 1));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/t4.jpg", 111112, "Gostas deste edificio?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("http://web.ist.utl.pt/~ist170638/tappitz/images/563e5f71d2425.jpg", 111112, "isto és virado para vcs?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/IMG_20151109_032605.jpg", 111112, "isto és virado para vcs, Já não?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));

        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/melancia_velho.jpg", 111112, "isto és virado para vcs?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));
        photos.add(new PhotoInbox("https://dl.dropboxusercontent.com/u/68830630/tAppitz/melancia_novo.jpg", 111112, "isto és virado para vcs, Já não?", "15/10/2015 - 12:35", false, "", "João Sampaio", 0));



               //notifico o adapter para atualizar a lista
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inbox, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public VerticalViewPager getViewPager() {
        return viewPager;
    }


    public interface OnNewPhotoReceived{
        public void refreshViewPager();
    }



}
