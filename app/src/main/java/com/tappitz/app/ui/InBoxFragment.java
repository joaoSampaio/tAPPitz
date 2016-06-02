package com.tappitz.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.adapter.InBoxPagerAdapter;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.model.UnseenNotifications;
import com.tappitz.app.rest.service.CallbackMultiple;
import com.tappitz.app.rest.service.DownloadPhotoService;
import com.tappitz.app.rest.service.ListInboxService;
import com.tappitz.app.util.ListenerPagerStateChange;
import com.tappitz.app.util.ModelCache;
import com.tappitz.app.util.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;


public class InBoxFragment extends Fragment {

    View rootView;
    private InBoxPagerAdapter adapter;
    private List<ReceivedPhoto> photos;
    private VerticalViewPager viewPager;
    private List<ListenerPagerStateChange> stateChange;
    private ListenerPagerStateChange stateOut;
    public InBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_in_box, container, false);

        Log.d("inbox", "**--new InBoxFragment:");


        photos = new ArrayList<>();
        adapter = new InBoxPagerAdapter(getChildFragmentManager(), photos);
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                removeUnseenNotification();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("myapp2", "**--onPageScrollStateChanged inBoxFragment:" + state);
                if (stateChange != null) {
                    for (ListenerPagerStateChange s : stateChange) {
                        s.onPageScrollStateChanged(state);
                    }
                }
            }

        });

        loadOffline();
        return rootView;
    }



    @Override
    public void onPause(){
        super.onPause();
        ((MainActivity)getActivity()).removeStateChange(stateOut);
    }


    @Override
    public void onResume(){
        super.onResume();

        stateOut = new ListenerPagerStateChange() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //voltamos a mostrar as opções
                    if (stateChange != null) {
                        for (ListenerPagerStateChange s : stateChange) {
                            s.onPageScrollStateChanged(state);
                        }
                    }
                }
            }
        };
        ((MainActivity)getActivity()).addStateChange(stateOut);

        ((MainActivity)getActivity()).setReloadInbox(new ReloadInbox() {
            @Override
            public void updateAfterVote() {
                loadOffline();
            }

            @Override
            public void updateNewReceivedPhoto(ReceivedPhoto photo) {
                refreshInbox();
            }

            @Override
            public void sharePicture() {
                share();
            }

            @Override
            public void openPageId(int id) {
                showPage(id);
            }

            @Override
            public void refreshOnline() {
                refreshInbox();
            }

            @Override
            public void InBoxSelected() {
                removeUnseenNotification();

            }
        });
    }


    @Override
    public void onStop(){
        super.onStop();
        ((MainActivity)getActivity()).setReloadInbox(null);
    }


    private void refreshInbox(){
//        rootView.findViewById(R.id.action_refresh).setEnabled(false);

        new ListInboxService(new CallbackMultiple<List<ReceivedPhoto>, String>() {
            @Override
            public void success(List<ReceivedPhoto> response) {

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
                    showPage(showPage);
//                    if(((MainActivity) getActivity()).getInbox_vote_id() >= 0){
//                        showPage(((MainActivity) getActivity()).getInbox_vote_id());
//                    }else{
//                        currentPage = (currentPage >= photos.size()) ? 0 : currentPage;
//                        viewPager.setCurrentItem(currentPage);
//                    }



                    //showPage(((MainActivity) getActivity()).getInbox_vote_id());

                    new ModelCache<List<ReceivedPhoto>>().saveModel(getActivity(), photos, Global.OFFLINE_INBOX);
                }
                rootView.findViewById(R.id.containerNoPhoto).setVisibility((photos.size() == 0)?View.VISIBLE: View.GONE);
            }

            @Override
            public void failed(String error) {
//                rootView.findViewById(R.id.action_refresh).setEnabled(true);
                rootView.findViewById(R.id.containerNoPhoto).setVisibility(View.VISIBLE);
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
            //((MainActivity)getActivity()).setInbox_vote_id(-1);
        }else{



            int pictureId = id;

            UnseenNotifications unseenNotifications = UnseenNotifications.load();
            if(unseenNotifications.getReceivedPhotos().remove(pictureId) != null){
                //já apagmos agora vamso fazer refresh
                Log.d("inbox", "refreshUnseenNotification");
                unseenNotifications.save();
                ((MainActivity)getActivity()).refreshUnseenNotification();
            }
        }

    }

    public void loadOffline(){
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
            adapter.notifyDataSetChanged();
            showPage(showPage);
        }

        if(photos.size() == 0){
            refreshInbox();
        }else{
            rootView.findViewById(R.id.containerNoPhoto).setVisibility(View.GONE);
        }

    }

    public void share(){
        if(viewPager != null){
            int showPage = viewPager.getCurrentItem(), id;
            if(photos.size() > showPage) {
                id = photos.get(showPage).getPictureId();

                new DownloadPhotoService(id, new CallbackMultiple<Intent, String>() {
                    @Override
                    public void success(Intent data) {
                        if(getActivity() != null){
                            MainActivity activity = (MainActivity)getActivity();
                            activity.showPage(Global.HOME);
                            activity.onActivityResult(Global.BROWSE_REQUEST, Activity.RESULT_OK, data);
                        }
                    }

                    @Override
                    public void failed(String error) {

                    }
                }).execute();

            }
        }
    }




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

        void sharePicture();

        void openPageId(int id);

        void refreshOnline();

        void InBoxSelected();
    }

    public void removeUnseenNotification(){
        Log.d("inbox", "removeUnseenNotification");
        if (!photos.isEmpty()) {
            int index = viewPager.getCurrentItem();
            ReceivedPhoto receivedPhoto = photos.get(index);
            int pictureId = receivedPhoto.getPictureId();

            UnseenNotifications unseenNotifications = UnseenNotifications.load();
            if(unseenNotifications.getReceivedPhotos().remove(pictureId) != null){
                //já apagmos agora vamso fazer refresh
                Log.d("inbox", "refreshUnseenNotification");
                unseenNotifications.save();
                ((MainActivity)getActivity()).refreshUnseenNotification();
            }
        }
    }


}
