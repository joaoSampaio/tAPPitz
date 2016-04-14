package com.tappitz.app.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.adapter.SelectSendPhotoAdapter;
import com.tappitz.app.app.AppController;
import com.tappitz.app.model.Contact;
import com.tappitz.app.util.ContactFilter;
import com.tappitz.app.util.ModelCache;

import java.util.List;


public class SelectContactFragment extends CustomDialogFragment implements CustomDialogFragment.AdapterWithFilter, View.OnClickListener {

    View rootView;
    private SelectSendPhotoAdapter adapter;
    private final static int[] CLICK = {R.id.action_send2, R.id.action_send, R.id.action_back, R.id.checkBox_select_all};
    OnSelectedContacts listener;
    private CheckBox select_all, checkBox_send_followers;
    private TextView textViewFollowers;

    public SelectContactFragment() {
        // Required empty public constructor
        super("Select Contacts");
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_select_contacts, container, false);

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
        textViewFollowers = (TextView)rootView.findViewById(R.id.textViewFollowers);
        int count = 0;
        List<Contact> followers = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(), new TypeToken<List<Contact>>() {
        }.getType(), Global.MYFOLLOWERS);
        if(followers != null && followers.size() > 0 && followers.get(0) instanceof Contact) {
            textViewFollowers.setText("All " + followers.size() + " Followers");
        }else{
            rootView.findViewById(R.id.layout_followers).setVisibility(View.GONE);
//            checkBox_send_followers.setVisibility(View.GONE);
//            textViewFollowers.setVisibility(View.GONE);
        }

        select_all = (CheckBox)rootView.findViewById(R.id.checkBox_select_all);

        for (int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);

        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewDescription.setText("Back");

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_send2:
                sendPhoto();
                break;
            case R.id.action_send:
                sendPhoto();
                break;
            case R.id.action_back:
                getDialog().dismiss();
                break;
            case R.id.checkBox_select_all:
                boolean all = ((CheckBox) v).isChecked();
                adapter.sellectAll(all);
                break;
        }
    }


    private void sendPhoto(){
        List<Integer> selected = adapter.getSelectedContacts();
        if (selected.size() > 0 || checkBox_send_followers.isChecked()) {
            if (listener != null) {
                (rootView.findViewById(R.id.action_send)).setEnabled(false);
                (rootView.findViewById(R.id.action_send2)).setEnabled(false);
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

    @Override
    public void onPause() {
        super.onPause();
        if(getDialog() != null)
            getDialog().dismiss();
    }

}
