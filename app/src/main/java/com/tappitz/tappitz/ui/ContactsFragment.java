package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactManagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.rest.service.SearchContactService;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.RecyclerClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ContactsFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private TextWatcher mSearchTw;
    private EditText mSearchEdt;
    private SwipeRefreshLayout swipeLayout;
    List<Contact> allContactsList;
    private TextView text_no_contact;
    private ContactManagerAdapter adapter;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
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
        rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        loadUI();
        refresh();
        return rootView;
    }

    private void loadUI(){
        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewDescription.setText("Contacts");
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

        allContactsList = new ArrayList<>();
//        final Drawable upArrow = ContextCompat.getDrawable(getActivity(),R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
//        ImageView img = (ImageView)rootView.findViewById(R.id.action_back);
//        Drawable backArrow = img.getDrawable();
//        if(backArrow != null){
//            backArrow.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
//        }


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
        });

        rv.setAdapter(adapter); // the data manager is assigner to the RV
        rv.addOnItemTouchListener( // and the click is handled
                new RecyclerClickListener(getActivity(), new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // STUB:
                        // The click on the item must be handled
                        Toast.makeText(getActivity(), "Clicked in " + position, Toast.LENGTH_SHORT).show();
                    }
                }));

        mSearchEdt = (EditText)rootView.findViewById(R.id.mSearchEdt);


        mSearchTw=new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                    // showToast("Pedido ao servidor");
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
        String contacts = "";
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
            contacts = sp.getString("contacts", "");
        }catch (Exception e){
            Log.d("myapp", "error:" + e.getMessage());
        }
        List<Contact> contactsList = new ArrayList<Contact>();
        if (contacts.equals("")) {
            return contactsList;
        } else {
            try {
                Type type = new TypeToken<List<Contact>>() {
                }.getType();
                contactsList = new Gson().fromJson(contacts, type);

                //check if is the intended type
                if(contactsList.get(0).getName() == null)
                    contactsList = new ArrayList<Contact>();
            } catch (Exception e) {
                e.printStackTrace();
                contactsList = new ArrayList<Contact>();
            }
            return contactsList;
        }

    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//    }

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
//        allContactsList = loadContactsOffline();
        notifyAdapter();
        new ListFriendsService(new CallbackMultiple<List<Contact>, String>() {
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
//                showToast(error);

            }
        }).execute();
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
        String searchParam = mSearchEdt.getText().toString();

        boolean alreadyExists = false;
        for (Contact c: allContactsList) {
            if(c.getEmail().equals(searchParam) || c.getUsername().equals(searchParam))
                alreadyExists = true;
        }
        if(!alreadyExists){
            new SearchContactService(searchParam, new CallbackMultiple<Contact, String>() {
                @Override
                public void success(Contact response) {

                    List<Contact> tmp = new ArrayList<Contact>();
                    if(response != null) {

                        response.setIsFollower(false);
                        response.setIsFriend(false);
                        tmp.add(response);
                    }

                    adapter.setContacts(tmp);
                    adapter.notifyDataSetChanged();
                    checkIfHasContacts(tmp.size());
                }

                @Override
                public void failed(String error) {
                    //showToast(error);
                }
            }).execute();
        }



        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }

    private void saveContactsOffline(List<Contact> contacts){

        Context ctx = AppController.getAppContext();
        new ModelCache<List<Contact>>().saveModel(ctx, contacts, Global.FRIENDS);


//        REVER
//        String json = new Gson().toJson(contacts);
//        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("contacts", json);
//        editor.commit();

    }

    public static void sortContacts(List<Contact> list){
        Collections.sort(list, new Comparator<Contact>() {
            @Override
            public int compare(Contact obj1, Contact obj2) {
                return obj1.getName().compareToIgnoreCase(obj2.getName());
            }
        });
    }

}
