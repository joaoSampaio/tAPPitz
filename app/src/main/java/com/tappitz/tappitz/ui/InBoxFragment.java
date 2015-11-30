package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.InBoxPagerAdapter;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;



public class InBoxFragment extends Fragment {

    View rootView;
    private InBoxPagerAdapter adapter;
    private List<PhotoInbox> photos;
    private VerticalViewPager viewPager;
    private List<ListenerPagerStateChange> stateChange;
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
        Log.d("myapp2", "**--new inBoxFragment:");
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

        ((Button)rootView.findViewById(R.id.action_back)).setText("Inbox");
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScreenSlidePagerActivity) getActivity()).showPage(Global.HOME);
            }
        });

        loadOffline();
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
        new ListInboxService(new CallbackMultiple<List<PhotoInbox>, String>() {
            @Override
            public void success(List<PhotoInbox> response) {
                if(response != null && response.size() > 0 && getActivity() != null) {
                    int currentPage = viewPager.getCurrentItem();
                    photos.clear();
                    photos.addAll(response);
                    adapter.notifyDataSetChanged();
                    currentPage = (currentPage >= photos.size()) ? 0 : currentPage;
                    viewPager.setCurrentItem(currentPage);
                    new ModelCache<List<PhotoInbox>>().saveModel(getActivity(), photos, Global.OFFLINE_INBOX);
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

    private void loadOffline(){
        Log.d("myapp", "**--loadOffline:");
        if(photos.size() == 0) {
            List<PhotoInbox> tmp = new ModelCache<List<PhotoInbox>>().loadModel(getActivity(),new TypeToken<List<PhotoInbox>>(){}.getType(), Global.OFFLINE_INBOX);
            if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof PhotoInbox) {
                Log.d("myapp", "**--loadOffline: inside ");
                photos.addAll(tmp);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void OnDoneLoading(){
//        viewPager.setCurrentItem(0);
//        photos.clear();
//        adapter.notifyDataSetChanged();
    }

    public void updateLocal(PhotoInbox newPhoto){
        PhotoInbox old = getItemWithId(newPhoto.getPictureId());
        if(old != null){

            old.setVote(newPhoto.getVote());
            old.setComment(newPhoto.getComment());
            old.setHasVoted(true);
        }

    }

    private PhotoInbox getItemWithId(int id){
        for(PhotoInbox p: photos){
            if(p.getPictureId() == id)
                return p;
        }
        return null;
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


    public interface OnNewPhotoReceived{
        public void refreshViewPager();
    }

//    public interface ListenerStateChange{
//        public void onPageScrollStateChanged(int state);
//    }


}
