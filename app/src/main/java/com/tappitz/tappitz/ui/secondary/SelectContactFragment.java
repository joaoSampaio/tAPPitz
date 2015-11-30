package com.tappitz.tappitz.ui.secondary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactAdapter;
import com.tappitz.tappitz.adapter.SelectContactAdapter;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListContactsService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SelectContactFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private SelectContactAdapter adapter;
    View rootView;
    private SwipeRefreshLayout swipeLayout;
    List<ListViewContactItem> allContactsList;
    private TextView text_no_contact;
    private OnSelectedContacts listener;

    public SelectContactFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_select_contact, container, false);
        getDialog().setTitle("Select contacts:");
        loadUI();

        refresh();

        return rootView;
    }

    private void loadUI(){

        allContactsList = new ArrayList<>();
        listView = (ListView) rootView.findViewById(R.id.list_contacts);
        adapter = new SelectContactAdapter(getActivity(), allContactsList, new ContactAdapter.OnUpdate() {
            @Override
            public void onNoContactsFound(int size) {
                Log.d("myapp", "onNoContactsFound: " );
                checkIfHasContacts(size);
            }

            @Override
            public void reloadFromServer() {
                refresh();
            }

            @Override
            public void addContact(String eMail, int id, String name) {

            }
        });

        listView.setAdapter(adapter);
        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);


        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        rootView.findViewById(R.id.nextTo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myapp", "nextTo: ");

                List<Integer> selected = adapter.getSelectedContacts();
                if(selected.size() > 0){
                    if(listener != null) {
                        ((Button)v).setEnabled(false);
                        listener.sendPhoto(selected);
                        //dismiss();
                    }
                }else{
                    Toast.makeText(getActivity(), "Select a contact", Toast.LENGTH_SHORT).show();
                    //mostrar toast de erro
                }
            }
        });

        rootView.findViewById(R.id.backToPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myapp", "backToPrevious: ");
                dismiss();
            }
        });


    }

    private void checkIfHasContacts(int size){
        Log.d("myapp", "checkIfHasContacts: " + allContactsList.size());

        text_no_contact.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
    }



    private void loadContacts(){
        new ListContactsService(new CallbackMultiple<List<ListViewContactItem>, String>() {
            @Override
            public void success(List<ListViewContactItem> response) {

                if(response.size() > 0)
                    response.add(0, new ListViewContactItem("My Contacts"));
                allContactsList.clear();
                allContactsList.addAll(response);
                adapter.notifyDataSetChanged();
                checkIfHasContacts(allContactsList.size());

                saveContactsOffline(allContactsList);

                swipeLayout.setRefreshing(false);
            }

            @Override
            public void failed(String error) {
                showToast(error);
                swipeLayout.setRefreshing(false);
            }
        }).execute();
    }

    private void refresh(){

        allContactsList.clear();
        allContactsList.addAll(loadContactsOffline());
        adapter.notifyDataSetChanged();
        checkIfHasContacts(allContactsList.size());
        if(allContactsList.size() == 0) {
            //pede a lista de todos os contactos
            loadContacts();
        }
    }

    public void showToast(String msg){
        try {
            if(getActivity() != null)
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(OnSelectedContacts listener) {
        this.listener = listener;
    }

    public interface OnSelectedContacts{
        public void sendPhoto(List<Integer> contacts);
    }

    private List<ListViewContactItem> loadContactsOffline(){
        String contacts = "";
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
            contacts = sp.getString("contacts", "");
        }catch (Exception e){
            Log.d("myapp", "error:" + e.getMessage());
        }
        if (contacts.equals("")) {
            return new ArrayList<ListViewContactItem>();
        } else {
            Type type = new TypeToken<List<ListViewContactItem>>() {
            }.getType();
            List<ListViewContactItem> contactsList = new Gson().fromJson(contacts, type);
            return contactsList;
        }
    }

    private void saveContactsOffline(List<ListViewContactItem> contacts){
        String json = new Gson().toJson(contacts);
        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("contacts", json);
        editor.commit();

    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
