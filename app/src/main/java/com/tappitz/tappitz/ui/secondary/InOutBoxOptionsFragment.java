package com.tappitz.tappitz.ui.secondary;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.background.BackgroundService;
import com.tappitz.tappitz.model.ImageModel;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.model.SentPicture;
import com.tappitz.tappitz.model.UnseenNotifications;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.RefreshUnseenNotifications;

import java.util.ArrayList;
import java.util.List;


public class InOutBoxOptionsFragment extends Fragment implements View.OnClickListener {

    View rootView;
    private String title;
    private int TYPE;
    private RefreshUnseenNotifications refreshUnseenNotifications;
    final static int[] CLICABLES = {R.id.layout_delete, R.id.layout_dynamic, R.id.layout_see_all};


    public InOutBoxOptionsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_in_and_out_options, container, false);

        if(getArguments() != null){
            title = getArguments().getString(Global.OPTIONS_TITLE);
            TYPE = getArguments().getInt(Global.OPTIONS_TYPE);
        }
        rootView.findViewById(R.id.imageShare).setVisibility((TYPE == Global.OPTIONS_TYPE_INBOX)? View.VISIBLE : View.GONE);
        if(TYPE == Global.OPTIONS_TYPE_INBOX){
            TextView share = (TextView)rootView.findViewById(R.id.action_dynamic);
            share.setText("Share");

            rootView.findViewById(R.id.layout_delete).setVisibility(View.INVISIBLE);


        }
        rootView.findViewById(R.id.textNotification).setVisibility((TYPE == Global.OPTIONS_TYPE_OUTBOX)? View.VISIBLE : View.GONE);
        if(TYPE == Global.OPTIONS_TYPE_OUTBOX){
            TextView share = (TextView)rootView.findViewById(R.id.action_dynamic);
            share.setText("Feedback");

            UnseenNotifications unseenNotifications = UnseenNotifications.load();
            ((TextView)rootView.findViewById(R.id.textNotification)).setText(""+unseenNotifications.getReceivedComment().size());

            rootView.findViewById(R.id.textNotification).setVisibility(unseenNotifications.getReceivedComment().size() > 0? View.VISIBLE : View.GONE);


        }
        TextView option_title = (TextView)rootView.findViewById(R.id.option_title);
        option_title.setText(title);

        for(int id: CLICABLES)
            rootView.findViewById(id).setOnClickListener(this);



        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(TYPE == Global.OPTIONS_TYPE_OUTBOX){
            refreshUnseenNotifications = new RefreshUnseenNotifications() {
                @Override
                public void onRefreshUnseenNotifications(UnseenNotifications unseenNotifications) {
                    ((TextView)rootView.findViewById(R.id.textNotification)).setText(""+unseenNotifications.getReceivedComment().size());
                }
            };
            ((ScreenSlidePagerActivity)getActivity()).addInterestUnseenNotification(refreshUnseenNotifications);
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        if(TYPE == Global.OPTIONS_TYPE_OUTBOX){
            ((ScreenSlidePagerActivity)getActivity()).removeInterestUnseenNotification(refreshUnseenNotifications);
        }
    }


    @Override
    public void onClick(View v) {
        View view;
        switch (v.getId()) {
            case R.id.layout_delete:
                if(!BackgroundService.isWifiAvailable()){
                    Toast.makeText(getActivity(), "Please connect to internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TYPE == Global.OPTIONS_TYPE_INBOX){
                    //if(((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener() != null)
                    // ((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener().refreshOnline();

                }
                if(TYPE == Global.OPTIONS_TYPE_OUTBOX){
                    //delete
                    if(((ScreenSlidePagerActivity)getActivity()).getReloadOutbox() != null)
                        ((ScreenSlidePagerActivity)getActivity()).getReloadOutbox().deletePhoto();

//                    Toast.makeText(getActivity(), "we are working on it...", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.layout_dynamic:
                if(TYPE == Global.OPTIONS_TYPE_INBOX)
                    //share
                    if(((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener() != null)
                        ((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener().sharePicture();
                if(TYPE == Global.OPTIONS_TYPE_OUTBOX) {
                    // see pending comments
//                    if (((ScreenSlidePagerActivity) getActivity()).getReloadOutbox() != null)
//                        ((ScreenSlidePagerActivity) getActivity()).getReloadOutbox().refreshOnline();
                    openGallery(ImageModel.TYPE_OUTBOX_NOTIFICATION, "", "Gallery_OUTBOX_NOTIFICATION");
                }

                break;
            case R.id.layout_see_all:

                String imagesData = "", tag="";
                int type = 0;
                if(TYPE == Global.OPTIONS_TYPE_INBOX){
                    List<ReceivedPhoto> tmp = new ModelCache<List<ReceivedPhoto>>().loadModel(getActivity(),new TypeToken<List<ReceivedPhoto>>(){}.getType(), Global.OFFLINE_INBOX);
                    List<ImageModel> images = new ArrayList<>();
                    if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof ReceivedPhoto) {
                        images = ReceivedPhoto.generateImageGallery(tmp);
                    }
                    Gson gson = new Gson();
                    imagesData = gson.toJson(images);

                    type = ImageModel.TYPE_INBOX;
                    tag = "Gallery_INBOX";
                }else {
                    List<SentPicture> tmp = new ModelCache<List<SentPicture>>().loadModel(getActivity(), new TypeToken<List<SentPicture>>() {
                    }.getType(), Global.OFFLINE_OUTBOX);
                    List<ImageModel> images = new ArrayList<>();
                    if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof SentPicture) {
                        images = SentPicture.generateImageGallery(tmp);
                    }
                    Gson gson = new Gson();
                    imagesData = gson.toJson(images);

                    type = ImageModel.TYPE_OUTBOX;
                    tag = "Gallery_OUTBOX";
                }



                openGallery(type, imagesData, tag);


                break;
        }
    }

    private void openGallery(int type, String imagesData, String tag){
        Bundle args = new Bundle();
        args.putString(GalleryFragment.GALLERY_ITEMS, imagesData);
        args.putInt(GalleryFragment.GALLERY_TYPE, type);

        DialogFragment newFragment = null;
        newFragment = GalleryFragment.newInstance(args);
        if(newFragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment.show(ft, tag);
        }
    }

}
