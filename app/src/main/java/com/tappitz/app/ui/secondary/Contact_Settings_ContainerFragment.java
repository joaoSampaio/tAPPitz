package com.tappitz.app.ui.secondary;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.app.R;


public class Contact_Settings_ContainerFragment extends Fragment implements View.OnClickListener {

    View rootView;
    final static int[] CLICKABLES = {R.id.action_followers, R.id.action_contacts,  R.id.action_following, R.id.action_add_contact};


    private final static int CONTACTS = 0;
    private final static int QRCODE = 1;
    private final static int SETTINGS = 2;

    public Contact_Settings_ContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact_settings_container, container, false);

        for (int id: CLICKABLES) {
            rootView.findViewById(id).setOnClickListener(this);
        }

//        rootView.findViewById(R.id.action_contacts).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("contacts");
//                if (prev != null) {
//                    ft.remove(prev);
//                }
//                ft.addToBackStack(null);
//
//                // Create and show the dialog.
//                DialogFragment newFragment = ContactsFragment.newInstance();
////                newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
////                android:theme="@style/CustomDialog"
//                newFragment.show(ft, "contacts");
//            }
//        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop(){
        super.onStop();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inbox, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        String tag = "";
        DialogFragment newFragment = null;
        switch (id){
            case R.id.action_contacts:
                //newFragment = ContactsFragment.newInstance();
                tag = "friends";
                break;
            case R.id.action_followers:

                newFragment = MyFollowersFragment.newInstance();
                tag = "followers";
                break;
            case R.id.action_following:
                newFragment = FollowingFragment.newInstance();
                tag = "following";
                break;
            case R.id.action_add_contact:
                newFragment = AddContactsDialogFragment.newInstance();
                tag = "add_contact";
                break;

        }

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
