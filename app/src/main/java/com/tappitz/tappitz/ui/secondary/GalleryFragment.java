package com.tappitz.tappitz.ui.secondary;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactManagerAdapter;
import com.tappitz.tappitz.adapter.GalleryAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.FutureWorkList;
import com.tappitz.tappitz.model.ImageModel;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.rest.service.SearchContactService;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.RecyclerItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        if(type == ImageModel.TYPE_INBOX){
            textViewDescription.setText("Sent");
        }else {
            textViewDescription.setText("Received");
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
        data = null;
        if(!dataList.equals("")){

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

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        Log.d("app", "clicked:"+position + " " + type);
//                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                        intent.putParcelableArrayListExtra("data", data);
//                        intent.putExtra("pos", position);
//                        startActivity(intent);

                        if(type == ImageModel.TYPE_INBOX){
                            if(getActivity() != null && ((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener() != null && data.size() > position) {
                                ((ScreenSlidePagerActivity) getActivity()).getReloadInboxListener().openPageId(data.get(position).getId());
                            }
                        }else {
                            Log.d("app", "getActivity() != null:"+ (getActivity() != null));
                            Log.d("app", "((ScreenSlidePagerActivity)getActivity()).getUpdateAfterPicture() != null:"+ (((ScreenSlidePagerActivity)getActivity()).getUpdateAfterPicture() != null));
                            Log.d("app", "data.size() > position:"+ (data.size() > position));
                            if(getActivity() != null && ((ScreenSlidePagerActivity)getActivity()).getUpdateAfterPicture() != null && data.size() > position) {
                                ((ScreenSlidePagerActivity) getActivity()).getUpdateAfterPicture().openPageId(data.get(position).getId());
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
        List<Contact> contactsList = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(),new TypeToken<List<Contact>>(){}.getType(), Global.FRIENDS);
        if(contactsList == null)
            contactsList = new ArrayList<>();
        return contactsList;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }


}