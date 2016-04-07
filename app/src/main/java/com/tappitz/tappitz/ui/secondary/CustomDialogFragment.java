package com.tappitz.tappitz.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactManagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFollowingService;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.rest.service.ListMyFollowersService;
import com.tappitz.tappitz.rest.service.SearchContactService;
import com.tappitz.tappitz.util.ContactFilter;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.RecyclerClickListener;
import com.tappitz.tappitz.util.SimpleDividerItemDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class CustomDialogFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private String title;


    private TextWatcher mSearchTw;
    public EditText mSearchEdt;
    private SwipeRefreshLayout swipeLayout;
    List<Contact> allContactsList;
    private TextView text_no_contact;


    public CustomDialogFragment() {
        // Required empty public constructor
    }

    public CustomDialogFragment(String title) {
        // Required empty public constructor
        this.title = title;
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
        allContactsList = new ArrayList<>();
        //loadUI();
        //refresh();
        return rootView;
    }

    public void loadUI(View rootView){
        this.rootView = rootView;

        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewDescription.setText(getTitle());

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


        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv); // layout reference

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true); // to improve performance
        rv.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), true));


        rv.setAdapter(getAdapter().getCustomAdapter()); // the data manager is assigner to the RV

        mSearchEdt = (EditText)rootView.findViewById(R.id.mSearchEdt);

        mSearchTw=new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                getAdapter().getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        if(mSearchEdt != null) {
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
        }

        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);


//        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
//        swipeLayout.setOnRefreshListener(this);

        refresh();
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


    public List<Contact> loadContactsOffline(String saveName){

        List<Contact> contactsList = new ModelCache<List<Contact>>().loadModel(AppController.getAppContext(),new TypeToken<List<Contact>>(){}.getType(), saveName);
        if(contactsList == null)
            contactsList = new ArrayList<>();

        if(allContactsList == null)
            allContactsList = new ArrayList<>();

        allContactsList.clear();
        allContactsList.addAll(contactsList);
        return allContactsList;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        notifyAdapter();
        CallbackMultiple<List<Contact>, String> callback = new CallbackMultiple<List<Contact>, String>() {
            @Override
            public void success(List<Contact> response) {

                Log.d("custom", "CallbackMultiple success");
                if(getActivity() != null) {
                    sortContacts(response);
                    allContactsList.clear();
                    allContactsList.addAll(response);
                    saveContactsOffline(allContactsList, getAdapter().getContactType());

//                    allContactsList.add(new Contact("Rui", "Ruiii", "rui@g.v", 22, true));
//                    allContactsList.add(new Contact("Rui", "Ruiii", "rui@g.v", 23, true));
//                    allContactsList.add(new Contact("Rui", "Ruiii", "rui@g.v", 24, true));
//                    allContactsList.add(new Contact("Rui", "Ruiii", "rui@g.v", 22, true));
//                    allContactsList.add(new Contact("Rui", "Ruiii", "rui@g.v", 25, true));
//                    allContactsList.add(new Contact("Rui2", "Ruiii", "rui@g.v", 28, true));

                    notifyAdapter();
                }
            }

            @Override
            public void failed(String error) {
//                showToast(error);

            }
        };

        if(getAdapter().getContactType().equals(Global.FRIENDS))
            new ListFriendsService(callback).execute();
        else if(getAdapter().getContactType().equals(Global.MYFOLLOWERS))
            new ListMyFollowersService(callback).execute();
        else if(getAdapter().getContactType().equals(Global.FOLLOWING))
            new ListFollowingService(callback).execute();


    }

    private void notifyAdapter() {
        Log.d("custom", "notifyAdapter");
        getAdapter().notifyDataSetChanged();
        checkIfHasContacts(allContactsList.size());
//        swipeLayout.setRefreshing(false);
    }

    public void checkIfHasContacts(int size){
        text_no_contact.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
        if(size == 0 && rootView.findViewById(R.id.layout_all) != null)
            rootView.findViewById(R.id.layout_all).setVisibility(View.GONE);
    }

    public abstract void searchContact();

    private void saveContactsOffline(List<Contact> contacts, String contactType){

        Context ctx = AppController.getAppContext();
        new ModelCache<List<Contact>>().saveModel(ctx, contacts, contactType);

    }

    public static void sortContacts(List<Contact> list){
        Collections.sort(list, new Comparator<Contact>() {
            @Override
            public int compare(Contact obj1, Contact obj2) {
                return obj1.getName().compareToIgnoreCase(obj2.getName());
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public abstract AdapterWithFilter getAdapter();

    public interface AdapterWithFilter{
        public void notifyDataSetChanged();
        public Filter getFilter();
        public RecyclerView.Adapter getCustomAdapter();
        public String getContactType();
    }
}
