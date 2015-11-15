package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactAdapter;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListContactRequestService;
import com.tappitz.tappitz.rest.service.ListContactsService;
import com.tappitz.tappitz.rest.service.SearchContactService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private ContactAdapter adapter;
    View rootView;
    private TextWatcher mSearchTw;
    private EditText mSearchEdt;
    private SwipeRefreshLayout swipeLayout;
    List<ListViewContactItem> allContactsList;
    List<ListViewContactItem> contactsFriends;
    List<ListViewContactItem> contactsRequest;
    private TextView text_no_contact;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        loadUI();

        //get contact list from server
        refresh();

        return rootView;
    }


    @Override
    public void onPause(){
        super.onPause();
        dismiss();
    }




    private void loadUI(){
        mSearchEdt = (EditText)rootView.findViewById(R.id.mSearchEdt);

        allContactsList = new ArrayList<>();
        contactsFriends = new ArrayList<>();
        contactsRequest = new ArrayList<>();
        listView = (ListView) rootView.findViewById(R.id.list_contacts);
        adapter = new ContactAdapter(getActivity(), allContactsList, new ContactAdapter.OnUpdate() {
            @Override
            public void onNoContactsFound(int size) {
                Log.d("myapp", "onNoContactsFound: " );
                checkIfHasContacts(size);
            }

            @Override
            public void reloadFromServer() {
                refresh();
            }
        });
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mSearchTw=new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("myapp", "onTextChanged: " );
                Log.d("myapp", "contactsFriends.size(): " + contactsFriends.size() );
                Log.d("myapp", "allContactsList.size(): " + allContactsList.size() );
                Log.d("myapp", "contactsRequest.size(): " + contactsRequest.size() );
                if(allContactsList.size() < (contactsFriends.size() + contactsRequest.size())){
                    allContactsList.clear();
                    allContactsList.addAll(contactsRequest);
                    allContactsList.addAll(contactsFriends);
                }
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mSearchEdt.addTextChangedListener(mSearchTw);
        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    showToast("Pedido ao servidor");
                    //Toast.makeText(getActivity(), "Pedido ao servidor", Toast.LENGTH_SHORT).show();
                    searchContact();
                    return true;
                }
                return false;
            }
        });


        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);


        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
//        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);





    }

    private void checkIfHasContacts(int size){
        Log.d("myapp", "checkIfHasContacts: " + allContactsList.size());

        text_no_contact.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
    }


    private void checkContactRequests(){
        new ListContactRequestService(new CallbackMultiple<List<ListViewContactItem>>() {
            @Override
            public void success(List<ListViewContactItem> response) {
                if(response.size() > 0)
                    response.add(0, new ListViewContactItem("Contact Requests"));
                contactsRequest = response;

                saveContactRequestsOffline(contactsRequest);

                notifyAdpater();
            }

            @Override
            public void failed(Object error) {
                showToast("Loading Offline contacts");

                contactsRequest = loadRequestsOffline();
                notifyAdpater();

                //Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void loadContacts(){
        new ListContactsService(new CallbackMultiple<List<ListViewContactItem>>() {
            @Override
            public void success(List<ListViewContactItem> response) {

                if(response.size() > 0)
                    response.add(0, new ListViewContactItem("My Contacts"));
                contactsFriends = response;

                saveContactsOffline(contactsFriends);

                notifyAdpater();
            }

            @Override
            public void failed(Object error) {
                contactsFriends = loadContactsOffline();
                notifyAdpater();
            }
        }).execute();
    }

    private void searchContact(){
        String searchParam = mSearchEdt.getText().toString();

        new SearchContactService(searchParam, new CallbackMultiple<Contact>() {
            @Override
            public void success(Contact response) {

//                if(contactsFriends.size() < allContactsList.size())
//                    contactsFriends.addAll(allContactsList);

                //adapter.removeFilter();
                //allContactsList.clear();
                List<ListViewContactItem> tmp = new ArrayList<ListViewContactItem>();
                if(response != null) {
                    tmp.add(0, new ListViewContactItem("Matches Found:"));
                    tmp.add(new ListViewContactItem(response));
                }
                else
                    tmp.add(0, new ListViewContactItem("No Matches Found:"));


                adapter.setContacts(tmp);
                adapter.notifyDataSetChanged();
                checkIfHasContacts(allContactsList.size());
            }

            @Override
            public void failed(Object error) {
                showToast("Erro2");
                //Toast.makeText(getActivity(), "Erro2", Toast.LENGTH_SHORT).show();
            }
        }).execute();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }

    private void refresh(){
        //pede a lista de pedidos de amizade pendentes
        checkContactRequests();

        //pede a lista de todos os contactos
        loadContacts();
    }

    public void showToast(String msg){
        try {
            if(getActivity() != null)
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveContactsOffline(List<ListViewContactItem> contacts){
        String json = new Gson().toJson(contacts);
        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("contacts", json);
        editor.commit();

    }

    private void saveContactRequestsOffline(List<ListViewContactItem> contactsRequest){
        String json = new Gson().toJson(contactsRequest);
        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("contactsRequest", json);
        editor.commit();

    }

    private List<ListViewContactItem> loadContactsOffline(){
        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String contacts = sp.getString("contacts", "");
        if(contacts.equals("")){
            return new ArrayList<ListViewContactItem>();
        }else {
            Type type = new TypeToken<List<ListViewContactItem >>(){}.getType();
            List<ListViewContactItem> contactsList = new Gson().fromJson(contacts, type);
            return contactsList;
        }
    }

    private List<ListViewContactItem> loadRequestsOffline(){
        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String contactsRequest = sp.getString("contactsRequest", "");
        if(contactsRequest.equals("")){
            return new ArrayList<ListViewContactItem>();
        }else {
            Type type = new TypeToken<List<ListViewContactItem >>(){}.getType();
            List<ListViewContactItem> contactsList = new Gson().fromJson(contactsRequest, type);
            return contactsList;
        }
    }


    private void notifyAdpater(){
        allContactsList.clear();
        allContactsList.addAll(contactsRequest);
        allContactsList.addAll(contactsFriends);
        adapter.notifyDataSetChanged();
        checkIfHasContacts(allContactsList.size());
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
