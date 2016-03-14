package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.InBoxPagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class InBoxFragment extends Fragment {

    View rootView;
    private InBoxPagerAdapter adapter;
    private List<ReceivedPhoto> photos;
    private VerticalViewPager viewPager;
    private List<ListenerPagerStateChange> stateChange;

    public InBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_in_box, container, false);

        Log.d("myappllllll", "**--new InBoxFragment:");


        photos = new ArrayList<>();
        adapter = new InBoxPagerAdapter(getChildFragmentManager(), photos);
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

        loadOffline();

//        PhotoInbox in = ((ScreenSlidePagerActivity) getActivity()).getNewPhoto();
//        if(in != null){
//            photos.add(0, in);
//            adapter.notifyDataSetChanged();
//            ((ScreenSlidePagerActivity) getActivity()).setNewPhoto(null);
//        }

//        refreshInbox();


        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((ScreenSlidePagerActivity)getActivity()).setReloadInbox(new ReloadInbox() {
            @Override
            public void updateAfterVote() {
                Log.d("myapp", "**--inbox updateAfterVote  ");
                loadOffline();
            }

            @Override
            public void updateNewReceivedPhoto(ReceivedPhoto photo) {
                refreshInbox();
            }

            @Override
            public void refreshOnline() {
                refreshInbox();
            }
        });
    }


    @Override
    public void onStop(){
        super.onStop();
        ((ScreenSlidePagerActivity)getActivity()).setReloadInbox(null);
    }


    private void refreshInbox(){
//        rootView.findViewById(R.id.action_refresh).setEnabled(false);
        Log.d("myappllllll", "**--refreshInbox:");

        new ListInboxService(new CallbackMultiple<List<ReceivedPhoto>, String>() {
            @Override
            public void success(List<ReceivedPhoto> response) {
                Log.d("myappllllll", "**--refreshInbox:success");

                if(response != null && response.size() > 0 && getActivity() != null) {
                    int showPage = 0;
                    if(viewPager != null){
                        int currentPage = viewPager.getCurrentItem();
                        if(photos.size() > currentPage)
                            showPage = photos.get(currentPage).getPictureId();
                    }


                    photos.clear();
                    photos.addAll(ReceivedPhoto.join(response));
                    adapter.notifyDataSetChanged();
                    Log.d("myappllllll", "**--refreshInbox:showPage:"+showPage);
                    showPage(showPage);
//                    if(((ScreenSlidePagerActivity) getActivity()).getInbox_vote_id() >= 0){
//                        showPage(((ScreenSlidePagerActivity) getActivity()).getInbox_vote_id());
//                    }else{
//                        currentPage = (currentPage >= photos.size()) ? 0 : currentPage;
//                        viewPager.setCurrentItem(currentPage);
//                    }



                    //showPage(((ScreenSlidePagerActivity) getActivity()).getInbox_vote_id());

                    new ModelCache<List<ReceivedPhoto>>().saveModel(getActivity(), photos, Global.OFFLINE_INBOX);
                }
            }

            @Override
            public void failed(String error) {
//                rootView.findViewById(R.id.action_refresh).setEnabled(true);
            }
        }).execute();
    }

    private void showPage(int id){
        int position = -1;
        int current = 0;
        if(id < 0)
            return;
        for (ReceivedPhoto in: photos) {
            if(in.getPictureId() == id){
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

    public void loadOffline(){
        Log.d("myapp", "**--inbox loadOffline  ");
        int showPage = 0;
        if(viewPager != null){
            showPage = viewPager.getCurrentItem();
            if(photos.size() > showPage)
                showPage = photos.get(showPage).getPictureId();
        }

        List<ReceivedPhoto> tmp = new ModelCache<List<ReceivedPhoto>>().loadModel(getActivity(),new TypeToken<List<ReceivedPhoto>>(){}.getType(), Global.OFFLINE_INBOX);
        if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof ReceivedPhoto) {

            photos.clear();
            photos.addAll(tmp);
            Log.d("myapp", "**--inbox has  " + photos.size());
            Gson gson = new Gson();
            Log.d("myapp", "**--inbox:   " + gson.toJson(tmp));
            adapter.notifyDataSetChanged();
            showPage(showPage);
        }

        if(photos.size() == 0){
            refreshInbox();
        }

    }

//    public void updateTemporaryLocal(ReceivedPhoto newPhoto){
//        ReceivedPhoto old = getItemWithId(newPhoto.getPictureId());
//        if(old != null){
//
//            old.setVote(newPhoto.getVote());
//            old.setComment(newPhoto.getComment());
//            old.setHasVoted(true);
//            old.setIsVoteTemporary(false);
//            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
//            old.setVotedDate(now);
//            adapter.notifyDataSetChanged();
//            new ModelCache<List<ReceivedPhoto>>().saveModel(getActivity(), photos, Global.OFFLINE_INBOX);
//        }
//
//    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inbox, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public VerticalViewPager getViewPager() {
        return viewPager;
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


    public interface ReloadInbox{
        void updateAfterVote();

        void updateNewReceivedPhoto(ReceivedPhoto photo);

        void refreshOnline();
    }

}
