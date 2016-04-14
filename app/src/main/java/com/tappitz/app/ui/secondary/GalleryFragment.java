package com.tappitz.app.ui.secondary;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.adapter.GalleryAdapter;
import com.tappitz.app.app.AppController;
import com.tappitz.app.model.Contact;
import com.tappitz.app.model.ImageModel;
import com.tappitz.app.model.SentPicture;
import com.tappitz.app.model.UnseenNotifications;
import com.tappitz.app.ui.ScreenSlidePagerActivity;
import com.tappitz.app.util.ModelCache;
import com.tappitz.app.util.RecyclerClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends DialogFragment {

    public static final String GALLERY_ITEMS  = "GALLERY_ITEMS";
    public static final String GALLERY_TYPE  = "GALLERY_TYPE";

    private View rootView;
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<ImageModel> data;
    private int type;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(Bundle args) {
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        loadUI();
        return rootView;
    }

    private void loadUI(){
        Bundle mArgs = getArguments();
        String dataList = mArgs.getString(GALLERY_ITEMS, "");
        type = mArgs.getInt(GALLERY_TYPE, 0);
        data = null;
        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        if(type == ImageModel.TYPE_INBOX){
            textViewDescription.setText("Sent");
        }else if(type == ImageModel.TYPE_OUTBOX) {
            textViewDescription.setText("Received");
        } else if(type == ImageModel.TYPE_OUTBOX_NOTIFICATION){
            textViewDescription.setText("Photos with new Comments");

            UnseenNotifications unseenNotifications = UnseenNotifications.load();
            List<SentPicture> tmp = new ModelCache<List<SentPicture>>().loadModel(getActivity(),new TypeToken<List<SentPicture>>(){}.getType(), Global.OFFLINE_OUTBOX);
            if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof SentPicture) {
                data = SentPicture.generateUnseenImageGallery(tmp, unseenNotifications.getReceivedComment());
            }

        }

        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        if(data == null && !dataList.equals("")){

            Gson gson = new Gson();
            Type typeClass = new TypeToken<List<ImageModel>>(){}.getType();
            data = gson.fromJson(dataList, typeClass);

        }

        if(data == null)
            data = new ArrayList<>();


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new GalleryAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(),
                new RecyclerClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        if(type == ImageModel.TYPE_INBOX || type == ImageModel.TYPE_INBOX_NOTIFICATION){
                            if(getActivity() != null && ((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener() != null && data.size() > position) {
                                ((ScreenSlidePagerActivity) getActivity()).getReloadInboxListener().openPageId(data.get(position).getId());
                                ((ScreenSlidePagerActivity) getActivity()).showPage(Global.INBOX);
                            }
                        }else {
                            if(getActivity() != null && ((ScreenSlidePagerActivity)getActivity()).getReloadOutbox() != null && data.size() > position) {
                                ((ScreenSlidePagerActivity) getActivity()).getReloadOutbox().openPageId(data.get(position).getId());
                                ((ScreenSlidePagerActivity) getActivity()).showPage(Global.OUTBOX);
                            }
                        }

                        getDialog().dismiss();
                    }
                }));


    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }


    private List<Contact> loadContactsOffline(){
        List<Contact> contactsList = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(), new TypeToken<List<Contact>>() {
        }.getType(), Global.FRIENDS);
        if(contactsList == null)
            contactsList = new ArrayList<>();
        return contactsList;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getDialog() != null)
            getDialog().dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
