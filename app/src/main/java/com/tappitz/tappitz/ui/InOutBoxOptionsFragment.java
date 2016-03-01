package com.tappitz.tappitz.ui;

import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.InBoxPagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListInboxService;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class InOutBoxOptionsFragment extends Fragment implements View.OnClickListener {

    View rootView;
    private String title;
    private int TYPE;

    final static int[] CLICABLES = {R.id.action_delete, R.id.action_show_list, R.id.action_see_all};


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

        TextView option_title = (TextView)rootView.findViewById(R.id.option_title);
        option_title.setText(title);

        for(int id: CLICABLES)
            rootView.findViewById(id).setOnClickListener(this);



        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

    }


    @Override
    public void onStop(){
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        View view;
        switch (v.getId()) {
            case R.id.action_delete:
                Toast.makeText(getActivity(), "future work ...", Toast.LENGTH_SHORT).show();

                break;
            case R.id.action_show_list:
                Toast.makeText(getActivity(), "refreshing ...", Toast.LENGTH_SHORT).show();
                if(TYPE == Global.OPTIONS_TYPE_INBOX)
                    if(((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener() != null)
                        ((ScreenSlidePagerActivity)getActivity()).getReloadInboxListener().refreshOnline();
                if(TYPE == Global.OPTIONS_TYPE_OUTBOX)
                    if(((ScreenSlidePagerActivity)getActivity()).getUpdateAfterPicture() != null)
                        ((ScreenSlidePagerActivity)getActivity()).getUpdateAfterPicture().refreshOnline();

                break;
            case R.id.action_see_all:

                break;
        }
    }
}
