package com.tappitz.tappitz.ui.secondary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactAdapter;
import com.tappitz.tappitz.adapter.SelectContactAdapter_old;
import com.tappitz.tappitz.adapter.SelectSendPhotoAdapter;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.ui.CustomDialogFragment;
import com.tappitz.tappitz.util.ContactFilter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SelectContactFragment extends CustomDialogFragment implements CustomDialogFragment.AdapterWithFilter {

    View rootView;
    private SelectSendPhotoAdapter adapter;
    OnSelectedContacts listener;


    public SelectContactFragment() {
        // Required empty public constructor
        super("Select Contacts");
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.select_contacts_fragment, container, false);

        adapter = new SelectSendPhotoAdapter(loadContactsOffline(), getActivity(), new ContactFilter.OnUpdate() {
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
        });


        rootView.findViewById(R.id.nextTo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Integer> selected = adapter.getSelectedContacts();
                if (selected.size() > 0) {
                    if (listener != null) {
                        ((Button) v).setEnabled(false);
                        listener.sendPhoto(selected);
                        //dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Select a contact", Toast.LENGTH_SHORT).show();
                    //mostrar toast de erro
                }
            }
        });

        rootView.findViewById(R.id.backToPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

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
        return Global.FRIENDS;
    }

    @Override
    public AdapterWithFilter getAdapter() {
        return this;
    }




    public void setListener(OnSelectedContacts listener) {
        this.listener = listener;
    }

    public interface OnSelectedContacts{
        public void sendPhoto(List<Integer> contacts);
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
