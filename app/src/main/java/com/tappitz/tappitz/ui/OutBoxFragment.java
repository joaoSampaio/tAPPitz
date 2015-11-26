package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.OutBoxPagerAdapter;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;
import com.tappitz.tappitz.rest.service.ListOutboxService;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class OutBoxFragment extends Fragment {


    View rootView;
    private OutBoxPagerAdapter adapter;
    private List<PhotoOutbox> photos;
    private List<ListenerPagerStateChange> stateChange;
    private VerticalViewPager viewPager;
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
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("myapp2", "**--seletcted inBoxFragment:" + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("myapp2", "**--onPageScrollStateChanged inBoxFragment:" + state);
                if (stateChange != null) {
                    for (ListenerPagerStateChange s : stateChange) {
                        s.onPageScrollStateChanged(state);
                    }
                } else
                    Log.d("myapp2", "**--stateChange is null:");
            }

        });
        refreshOutbox();


        rootView.findViewById(R.id.action_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshOutbox();
            }
        });




        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void refreshOutbox(){
        new ListOutboxService(new CallbackMultiple<List<PhotoOutbox>, String>() {
            @Override
            public void success(List<PhotoOutbox> response) {
                if(response != null && response.size() > 0) {
                    viewPager.setCurrentItem(0);
                    photos.clear();
                    photos.addAll(response);
                    //viewPager.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else {
                    OnDoneLoading();
                }


            }

            @Override
            public void failed(String error) {
                OnDoneLoading();
            }
        }).execute();
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

    public List<ListenerPagerStateChange> getStateChange() {
        return stateChange;
    }

    public void addStateChange(ListenerPagerStateChange stateChange) {
        if(this.stateChange == null)
            this.stateChange = new ArrayList<ListenerPagerStateChange>();
        this.stateChange.add(stateChange);
    }
    public void removeStateChange(ListenerPagerStateChange stateChange) {
        if(this.stateChange != null) {
            this.stateChange.remove(stateChange);
        }
    }


}
