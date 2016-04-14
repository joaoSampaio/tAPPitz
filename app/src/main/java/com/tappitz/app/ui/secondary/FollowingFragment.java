package com.tappitz.app.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;

import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.adapter.ContactManagerAdapter;
import com.tappitz.app.util.ContactFilter;


public class FollowingFragment extends CustomDialogFragment implements CustomDialogFragment.AdapterWithFilter {

    View rootView;
    private ContactManagerAdapter adapter;

    public FollowingFragment() {
        // Required empty public constructor
        super("Following");
    }

    public static FollowingFragment newInstance() {
        FollowingFragment fragment = new FollowingFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contacts_fragment, container, false);


        adapter = new ContactManagerAdapter(loadContactsOffline(getContactType()), new ContactFilter.OnUpdate() {
            @Override
            public void onNoContactsFound(int size) {
                checkIfHasContacts(size);
            }

            @Override
            public void reloadFromServer() {

            }

            @Override
            public void addContact(String eMail, int id, String name) {

            }
        }, getActivity(), null);


        loadUI(rootView);


        return rootView;
    }




    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return adapter.getFilter();
    }

    @Override
    public RecyclerView.Adapter getCustomAdapter() {
        return adapter;
    }

    @Override
    public String getContactType() {
        return Global.FOLLOWING;
    }

    @Override
    public AdapterWithFilter getAdapter() {
        return this;
    }



    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void searchContact(){

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }
}
