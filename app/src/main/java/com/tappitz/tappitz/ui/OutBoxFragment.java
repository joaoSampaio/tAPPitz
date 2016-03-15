package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.OutBoxPagerAdapter;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.model.SentPicture;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.DeletePhotoService;
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
    private List<SentPicture> photos;
    private List<ListenerPagerStateChange> stateChange;
    private VerticalViewPager viewPager;
    private ListenerPagerStateChange stateOut;
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

        ((ScreenSlidePagerActivity)getActivity()).setUpdateAfterPicture(new UpdateAfterPicture() {
            @Override
            public void updateTemporaryOutbox(SentPicture outbox) {

                //se nao existir
                if(!outbox.belongsTo(photos)){
                        photos.add(0, outbox);
                        adapter.notifyDataSetChanged();
                        //guardamos offline a nova foto
                        new ModelCache<List<SentPicture>>().saveModel(getActivity(), photos, Global.OFFLINE_OUTBOX);
                    }
            }

            @Override
            public void refreshOnline() {
                refreshOutbox();
            }

            @Override
            public void deletePhoto() {
                final int showPage = viewPager.getCurrentItem();

                if (photos.size() > showPage) {
                    String date = photos.get(showPage).getTimeAgo();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want to delete the photo created " + date + " ?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    delete();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog

                                }
                            });
                    builder.setTitle("Delete Photo");
                    builder.create();
                    builder.show();
                }
            }

            @Override
            public void openPageId(int id) {
                Log.d("app", "showPage outbox:");
                showPage(id);
            }

            @Override
            public void refreshOfflineOutbox() {
                if(photos != null){

                    List<SentPicture> tmp = new ModelCache<List<SentPicture>>().loadModel(getActivity(), new TypeToken<List<SentPicture>>() {
                    }.getType(), Global.OFFLINE_OUTBOX);
                    if(tmp != null) {
                        int currentPage = viewPager.getCurrentItem();

                        photos.clear();
                        photos.addAll(tmp);
                        adapter.notifyDataSetChanged();
                        currentPage = (currentPage >= photos.size()) ? 0 : currentPage;
                        viewPager.setCurrentItem(currentPage);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void refreshOutbox(){
        new ListOutboxService(new CallbackMultiple<List<SentPicture>, String>() {
            @Override
            public void success(List<SentPicture> serverPictures) {
                if(serverPictures != null && serverPictures.size() > 0 && getActivity() != null) {
                    int showPage = 0;
                    if(viewPager != null){
                        int currentPage = viewPager.getCurrentItem();
                        if(photos.size() > currentPage)
                            showPage = photos.get(currentPage).getId();
                    }
                    List<SentPicture> offlinePictures = new ModelCache<List<SentPicture>>().loadModel(getActivity(),new TypeToken<List<SentPicture>>(){}.getType(), Global.OFFLINE_OUTBOX);
                    if(offlinePictures != null && offlinePictures.size() > 0 && offlinePictures.get(0) instanceof SentPicture) {
                        SentPicture.join(serverPictures, offlinePictures);
                    }

                    photos.clear();
                    photos.addAll(serverPictures);
                    adapter.notifyDataSetChanged();
                    showPage(showPage);
                    new ModelCache<List<SentPicture>>().saveModel(getActivity(), photos, Global.OFFLINE_OUTBOX);
                }
            }

            @Override
            public void failed(String error) {
//                rootView.findViewById(R.id.action_refresh).setEnabled(true);
            }
        }).execute();
    }

    private void loadOffline(){
        Log.d("myapp", "**--loadOffline:");
        if(photos.size() == 0) {
            List<SentPicture> tmp = new ModelCache<List<SentPicture>>().loadModel(getActivity(),new TypeToken<List<SentPicture>>(){}.getType(), Global.OFFLINE_OUTBOX);
            if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof SentPicture) {
                photos.addAll(tmp);
                adapter.notifyDataSetChanged();
            }
        }
        if(photos.size() == 0) {
            refreshOutbox();
        }
    }

    private void delete(){
        if(viewPager != null) {
            final int showPage = viewPager.getCurrentItem(), id;
            if (photos.size() > showPage) {
                id = photos.get(showPage).getId();
                new DeletePhotoService(id, new CallbackMultiple() {
                    @Override
                    public void success(Object response) {
                        SentPicture.removeId(photos, id);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failed(Object error) {

                    }
                }).execute();
            }
        }
    }

    private void showPage(int id){
        int position = -1;
        int current = 0;
        if(id < 0)
            return;
        for (SentPicture in: photos) {
            if(in.getId() == id){
                position = current;
                break;
            }
            current++;
        }
        Log.d("myapp", "**--showPage id"+id+" page:" + position);

        //foi encontrada a imagem vamos mostra-la
        if(position >= 0){
            viewPager.setCurrentItem(position);
            //((ScreenSlidePagerActivity)getActivity()).setInbox_vote_id(-1);
        }

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

    @Override
    public void onResume(){
        super.onResume();
        stateOut = new ListenerPagerStateChange() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //voltamos a mostrar as opções
                    Log.d("myapp2", "**--outboxpage  :" + state);
                    if (stateChange != null) {
                        for (ListenerPagerStateChange s : stateChange) {
                            s.onPageScrollStateChanged(state);
                        }
                    }
                }
            }
        };

        ((ScreenSlidePagerActivity)getActivity()).addStateChange(stateOut);
    }

    @Override
    public void onPause(){
        super.onPause();
        ((ScreenSlidePagerActivity)getActivity()).removeStateChange(stateOut);
    }


    public interface UpdateAfterPicture{
        void updateTemporaryOutbox(SentPicture outbox);

        void refreshOnline();

        void deletePhoto();

        void openPageId(int id);

        void refreshOfflineOutbox();
    }


}
