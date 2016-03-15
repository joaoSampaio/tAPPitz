package com.tappitz.tappitz.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.SelectSendPhotoAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;

import java.util.List;


public class SelectContactFragment extends CustomDialogFragment implements CustomDialogFragment.AdapterWithFilter {

    View rootView;
    private SelectSendPhotoAdapter adapter;
    OnSelectedContacts listener;
    private CheckBox select_all, checkBox_send_followers;

    public SelectContactFragment() {
        // Required empty public constructor
        super("Select Contacts");
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.select_contacts_fragment, container, false);

        adapter = new SelectSendPhotoAdapter(loadContactsOffline(getContactType()), getActivity(), new ContactFilter.OnUpdate() {
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

        checkBox_send_followers = (CheckBox)rootView.findViewById(R.id.checkBox_send_followers);

        int count = 0;
        List<Contact> followers = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(), new TypeToken<List<Contact>>() {
        }.getType(), Global.MYFOLLOWERS);
        Gson gson = new Gson();
        Log.d("sendPhoto", "+++++++++++++++++++++++++++++++++++++followers: ->" + gson.toJson(followers));
        if(followers != null && followers.size() > 0 && followers.get(0) instanceof Contact) {
            checkBox_send_followers.setText("Send to " + followers.size() + " Followers");
        }else{
            checkBox_send_followers.setVisibility(View.GONE);
        }


        select_all = (CheckBox)rootView.findViewById(R.id.checkBox_select_all);
        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean all = ((CheckBox) v).isChecked();
                adapter.sellectAll(all);
            }
        });
        rootView.findViewById(R.id.nextTo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Integer> selected = adapter.getSelectedContacts();
                if (selected.size() > 0) {
                    if (listener != null) {
                        ((Button) v).setEnabled(false);
                        boolean sendFollowers = (checkBox_send_followers).isChecked();
                        Log.d("sendPhoto", "+++++++++++++++++++++++++++++++++++++sendFollowers: ->" + sendFollowers);

                        listener.sendPhoto(selected, sendFollowers);
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
        public void sendPhoto(List<Integer> contacts, boolean sendToFollowers);
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
