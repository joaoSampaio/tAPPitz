package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.OutBoxPagerAdapter;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;
import com.tappitz.tappitz.rest.service.ListOutboxService;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.lang.reflect.Type;
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

        loadOffline();
        if(((ScreenSlidePagerActivity)getActivity()).getOutbox_id() >= 0){
            showPage(((ScreenSlidePagerActivity)getActivity()).getOutbox_id());
        }else {
            refreshOutbox();
        }
        ((Button)rootView.findViewById(R.id.action_back)).setText("Sent");
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScreenSlidePagerActivity) getActivity()).showPage(Global.HOME);
            }
        });
        rootView.findViewById(R.id.action_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
                refreshOutbox();
            }
        });
        rootView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
            }
        });

        ((ScreenSlidePagerActivity)getActivity()).setUpdateAfterPicture(new UpdateAfterPicture() {
            @Override
            public void updateOutbox(PhotoOutbox outbox) {
                if(photos != null){
                    if(!hasPhoto(outbox.getId())){
                        photos.add(0, outbox);
                        adapter.notifyDataSetChanged();
                        //guardamos offline a nova foto
                        new ModelCache<List<PhotoOutbox>>().saveModel(getActivity(), photos, Global.OFFLINE_OUTBOX);

                    }
                }
            }
        });
        return rootView;
    }

    private boolean hasPhoto(int id){
        for (PhotoOutbox out: photos) {
           if(out.getId() == id)
               return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void refreshOutbox(){
        rootView.findViewById(R.id.action_refresh).setEnabled(false);
        new ListOutboxService(new CallbackMultiple<List<PhotoOutbox>, String>() {
            @Override
            public void success(List<PhotoOutbox> response) {
                if(response != null && response.size() > 0 && getActivity() != null) {
                    int currentPage = viewPager.getCurrentItem();

                    photos.clear();
                    photos.addAll(response);
                    adapter.notifyDataSetChanged();
                    currentPage = (currentPage >= photos.size()) ? 0 : currentPage;
                    viewPager.setCurrentItem(currentPage);
                    new ModelCache<List<PhotoOutbox>>().saveModel(getActivity(), photos, Global.OFFLINE_OUTBOX);
                    rootView.findViewById(R.id.action_refresh).setEnabled(true);
                }
            }

            @Override
            public void failed(String error) {
                rootView.findViewById(R.id.action_refresh).setEnabled(true);
            }
        }).execute();
    }

    private void loadOffline(){
        Log.d("myapp", "**--loadOffline:");
        if(photos.size() == 0) {
            List<PhotoOutbox> tmp = new ModelCache<List<PhotoOutbox>>().loadModel(getActivity(),new TypeToken<List<PhotoOutbox>>(){}.getType(), Global.OFFLINE_OUTBOX);
            if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof PhotoOutbox) {
                Log.d("myapp", "**--loadOffline: inside ");
                photos.addAll(tmp);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void showPage(int id){
        int position = -1;
        int current = 0;
        if(id < 0)
            return;
        for (PhotoOutbox in: photos) {
            if(in.getId() == id){
                position = current;
                break;
            }
            current++;
        }

        //foi encontrada a imagem vamos mostra-la
        if(position >= 0){
            viewPager.setCurrentItem(position);
            //((ScreenSlidePagerActivity)getActivity()).setInbox_vote_id(-1);
        }

    }
    private void OnDoneLoading(){

//        adapter.notifyDataSetChanged();
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


    public interface UpdateAfterPicture{
        void updateOutbox(PhotoOutbox outbox);
    }




    private class UploadImageAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Glide.get(getActivity()).clearDiskCache();
            Glide.get(getActivity()).clearMemory();
            return null;
        }
    }


}
