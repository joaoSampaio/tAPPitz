package com.tappitz.tappitz.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactManagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.model.FutureWorkList;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFollowingService;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.rest.service.ListMyFollowersService;
import com.tappitz.tappitz.rest.service.OperationContactService;
import com.tappitz.tappitz.rest.service.SearchContactService;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.RecyclerClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public final static int FRIENDS = 0;
    public final static int FOLLOWING = 1;
    private int TYPE = 0;
    private View rootView;
//    private TextWatcher mSearchTw;
//    private EditText mSearchEdt;
    private SwipeRefreshLayout swipeLayout;
    List<Contact> allContactsList;
    private TextView text_no_contact;
    private ContactManagerAdapter adapter;
    private Handler handler;
    private String newSearch;
    private Button action_follow;





    private View progress_search, searchContainer, contact_search, progressOperation;
    private TextView mName, mUsername, mCircle, text_no_contact_search;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ContactsFragment.
     */
    public static ContactsFragment newInstance(int TYPE) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.setTYPE(TYPE);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        Log.d("ContactsFragment", "ContactsFragment TYPE: "+TYPE );
        loadUI();
        refresh();
        return rootView;
    }

    private void loadUI(){


        mName = (TextView) rootView.findViewById(R.id.CONTACT_name);
        mUsername = (TextView) rootView.findViewById(R.id.CONTACT_username);
        mCircle = (TextView)rootView.findViewById(R.id.CONTACT_circle);
        searchContainer = rootView.findViewById(R.id.searchContainer);
        progress_search = rootView.findViewById(R.id.progress_search);
        searchContainer.setVisibility(View.GONE);
        action_follow = (Button)rootView.findViewById(R.id.action_follow);
        contact_search = rootView.findViewById(R.id.contact_search);
        text_no_contact_search = (TextView) rootView.findViewById(R.id.text_no_contact_search);
        progressOperation = rootView.findViewById(R.id.progressOperation);

        handler = new Handler();
        newSearch = "";
        allContactsList = new ArrayList<>();
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv); // layout reference

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true); // to improve performance

        this.adapter = new ContactManagerAdapter(allContactsList, new ContactFilter.OnUpdate() {
            @Override
            public void onNoContactsFound(int size) {
                Log.d("myapp", "onNoContactsFound: " );
                checkIfHasContacts(size);
            }

            @Override
            public void reloadFromServer() {

            }

            @Override
            public void addContact(String eMail, int id, String name) {

            }
        }, getActivity());

        rv.setAdapter(adapter); // the data manager is assigner to the RV


        ((ContactContainerFragment)getParentFragment()).addSearchListener(new ContactContainerFragment.SearchText() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", " ->" + new String(s.toString()) + "  getItemCount:"+adapter.getItemCount());

                for(Contact c : adapter.getContacts()){
                    Log.d("contact", " ---------->" + c.getEmail() + " getUsername:"+c.getUsername());
                }

                //se for following e nao exitir mais contactos entao vamso fazer search
                if (TYPE == FOLLOWING && adapter.getItemCount() == 0) {

                    adapter.getFilter().filter(s);
                    if(adapter.getItemCount() > 0)
                        return;


                    Log.d("ADDContact", "TYPE == FOLLOWING && adapter.getContacts().size() == 0");
                    newSearch = new String(s.toString());
                    final String search = new String(s.toString());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (newSearch.equals(search)) {
                                Log.d("ADDContact", "text changed is the same as 1s ago:" + newSearch + "||||" + search);
                                searchContact();
                            } else {
                                Log.d("ADDContact", "text changed in less than 1s");
                            }
                        }
                    }, 1500);

                } else {
                    Log.d("ADDContact", "else");
                    adapter.getFilter().filter(s);
                }

            }
        });



//        mSearchEdt.addTextChangedListener(mSearchTw);
//        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    // showToast("Pedido ao servidor");
//                    //Toast.makeText(getActivity(), "Pedido ao servidor", Toast.LENGTH_SHORT).show();
//                    searchContact();
//                    return true;
//                }
//                return false;
//            }
//        });


        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);


        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);


        Log.d("ContactsFragment", "ContactsFragment: end");
    }



    @Override
    public void onStart() {
        super.onStart();
    }


    private List<Contact> loadContactsOffline(){
        List<Contact> contactsList = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(),new TypeToken<List<Contact>>(){}.getType(), getOfflineId());
        if(contactsList == null)
            contactsList = new ArrayList<>();
        Log.d("ContactsFragment", "loadContactsOffline: ");
        return contactsList;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        refresh();
    }


    public void refresh(){
        //pede a lista de todos os contactos
        loadContacts();
    }

    private void loadContacts(){
        allContactsList.clear();
        allContactsList.addAll(loadContactsOffline());
        notifyAdapter();

        CallbackMultiple callback = new CallbackMultiple<List<Contact>, String>() {
            @Override
            public void success(List<Contact> response) {

                sortContacts(response);
                allContactsList.clear();
                allContactsList.addAll(response);
                saveContactsOffline(allContactsList);
                notifyAdapter();
            }

            @Override
            public void failed(String error) {
            }
        };


        if(TYPE == FOLLOWING)
            new ListFollowingService(callback).execute();
        else
            new ListFriendsService(callback).execute();


    }

    private void notifyAdapter(){
        adapter.notifyDataSetChanged();
        checkIfHasContacts(allContactsList.size());
        swipeLayout.setRefreshing(false);
    }

    private void checkIfHasContacts(int size){
        Log.d("myapp", "checkIfHasContacts: " + allContactsList.size());

        text_no_contact.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
    }



    private void searchContact(){
        final String searchParam = newSearch;
        if(searchParam.isEmpty())
            return;

        searchContainer.setVisibility(View.VISIBLE);
        progress_search.setVisibility(View.VISIBLE);
        contact_search.setVisibility(View.GONE);
        text_no_contact.setVisibility(View.GONE);
        new SearchContactService(searchParam, new CallbackMultiple<Contact, String>() {
            @Override
            public void success(final Contact contact) {
                Log.d("myapp", "searchContact success: ");
                if(getActivity() != null && contact != null) {
                    contact_search.setVisibility(View.VISIBLE);
                    progress_search.setVisibility(View.GONE);
                    Log.d("myapp", "searchContact success: dentro ");
                    text_no_contact_search.setVisibility(View.GONE);
                    contact.setIsFollower(false);
                    contact.setIsFriend(false);
                    mName.setText(contact.getName());
                    mUsername.setText(contact.getUsername());

                    mCircle.setText(contact.getLetters());

                    GradientDrawable bgShape = (GradientDrawable) mCircle.getBackground();
                    bgShape.setColor(Color.parseColor("#33b5e5"));

                    action_follow.setVisibility(View.VISIBLE);
                    action_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressOperation.setVisibility(View.VISIBLE);
                            action_follow.setVisibility(View.GONE);
                            new OperationContactService(new ContactSendId(contact.getId(), Global.OPERATION_TYPE_INVITE), new CallbackMultiple<Boolean, String>() {
                                @Override
                                public void success(Boolean response) {
                                    if(getActivity() != null)
                                        progressOperation.setVisibility(View.GONE);

                                    Toast.makeText(AppController.getAppContext(), "Follow successful", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failed(String error) {
                                    if(getActivity() != null)
                                        progressOperation.setVisibility(View.GONE);
                                    Toast.makeText(AppController.getAppContext(), " "+ error, Toast.LENGTH_LONG).show();
                                }
                            }).execute();
                        }
                    });

                }


            }

            @Override
            public void failed(String error) {
                if(getActivity() != null) {
                    progress_search.setVisibility(View.GONE);
                    text_no_contact_search.setText("No matches found for "+ searchParam);
                    text_no_contact_search.setVisibility(View.VISIBLE);
                    //searchContainer.setVisibility(View.GONE);

                }
            }
        }).execute();




//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }



//    private void searchContact(){
//        String searchParam = mSearchEdt.getText().toString();
//
//        boolean alreadyExists = false;
//        for (Contact c: allContactsList) {
//            if(c.getEmail().equals(searchParam) || c.getUsername().equals(searchParam))
//                alreadyExists = true;
//        }
//        if(!alreadyExists){
//            new SearchContactService(searchParam, new CallbackMultiple<Contact, String>() {
//                @Override
//                public void success(Contact response) {
//
//                    List<Contact> tmp = new ArrayList<Contact>();
//                    if(response != null) {
//
//                        response.setIsFollower(false);
//                        response.setIsFriend(false);
//                        tmp.add(response);
//                    }
//
//                    adapter.setContacts(tmp);
//                    adapter.notifyDataSetChanged();
//                    checkIfHasContacts(tmp.size());
//                }
//
//                @Override
//                public void failed(String error) {
//                    //showToast(error);
//                }
//            }).execute();
//        }
//
//
//
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
//
//    }

    private void saveContactsOffline(List<Contact> contacts){

        Context ctx = AppController.getAppContext();
        new ModelCache<List<Contact>>().saveModel(ctx, contacts, getOfflineId());


    }

    public static void sortContacts(List<Contact> list){
        Collections.sort(list, new Comparator<Contact>() {
            @Override
            public int compare(Contact obj1, Contact obj2) {
                return obj1.getName().compareToIgnoreCase(obj2.getName());
            }
        });
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    private String getOfflineId(){
        if(TYPE == FOLLOWING)
            return Global.FRIENDS;
        else
            return Global.MYFOLLOWERS;
    }
}
