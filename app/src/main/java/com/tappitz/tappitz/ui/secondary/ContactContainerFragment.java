package com.tappitz.tappitz.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactPagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListFriendsService;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ModelCache;

import java.util.ArrayList;
import java.util.List;


public class ContactContainerFragment extends Fragment {

    View rootView;
//    final static int[] CLICKABLES = {R.id.action_followers, R.id.action_contacts,  R.id.action_following, R.id.action_add_contact};
    private TextWatcher mSearchTw;
    private EditText mSearchEdt;
    ViewPager viewPager;
    ContactPagerAdapter adapter;
    private List<SearchText> searchListener;
    private List<Contact> friends;
    private List<NotifyReturnedFriends> listenner;
    private List<ContactsFragment.ReloadContacts> reloadChildContacts;
    public ContactContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact_container, container, false);
        searchListener = new ArrayList<>();
        listenner = new ArrayList<>();
        friends = new ArrayList<>();
        reloadChildContacts = new ArrayList<>();
        loadContacts();

        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ContactContainer", "onclick");
                if(((ScreenSlidePagerActivity)getActivity()).getMiddleShowPage() != null){
                    ((ScreenSlidePagerActivity)getActivity()).getMiddleShowPage().showPage(Global.MIDDLE_BLANK);
                }
            }
        });


//        for (int id: CLICKABLES) {
//            rootView.findViewById(id).setOnClickListener(this);
//        }

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("FOLLOWING"));
        tabLayout.addTab(tabLayout.newTab().setText("FOLLOWERS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        adapter = new ContactPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mSearchTw=new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (SearchText listener: searchListener)
                listener.onTextChanged(s, start, before, count);
//                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mSearchEdt = (EditText)rootView.findViewById(R.id.searchEdt);
        mSearchEdt.addTextChangedListener(mSearchTw);
        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // showToast("Pedido ao servidor");
                    Toast.makeText(getActivity(), "Pedido ao servidor", Toast.LENGTH_SHORT).show();
                    //searchContact();
                    return true;
                }
                return false;
            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ScreenSlidePagerActivity)getActivity()).setReloadAllContactsFragments(new ReloadAllContactsFragments() {
            @Override
            public void onReloadAllContactsFragments() {
                reloadChildren();
            }
        });
    }


    @Override
    public void onPause(){
        super.onPause();
        searchListener.clear();
        ((ScreenSlidePagerActivity)getActivity()).setReloadAllContactsFragments(null);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inbox, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public interface SearchText{
        void onTextChanged(CharSequence s, int start, int before, int count);
    }

    public void addSearchListener(SearchText listener){
        searchListener.add(listener);
    }

    private void loadContacts(){


        CallbackMultiple callback = new CallbackMultiple<List<Contact>, String>() {
            @Override
            public void success(List<Contact> response) {


                new ModelCache<List<Contact>>().saveModel(AppController.getAppContext(), response, Global.FRIENDS);
                friends.addAll(response);
                for(NotifyReturnedFriends notify : listenner){
                    notify.onFriendsReceived(friends);
                }
            }

            @Override
            public void failed(String error) {
            }
        };

        new ListFriendsService(callback).execute();
    }


    public interface NotifyReturnedFriends {
        void onFriendsReceived(List<Contact> contacts);
    }

    public void addListenner(NotifyReturnedFriends listenner) {
        this.listenner.add(listenner);
    }
    public void removeListenner(NotifyReturnedFriends listenner) {
        this.listenner.remove(listenner);
    }

    public void addReloadContacts(ContactsFragment.ReloadContacts listenner) {
        this.reloadChildContacts.add(listenner);
    }
    public void removeReloadContacts(ContactsFragment.ReloadContacts listenner) {
        this.reloadChildContacts.remove(listenner);
    }



    public List<Contact> getFriends() {
        return friends;
    }


    public void reloadChildren(){
        Log.d("ContactContainer", "reloadChildren");
//        mSearchEdt.removeTextChangedListener(mSearchTw);
        mSearchEdt.setText("");
//        mSearchEdt.addTextChangedListener(mSearchTw);
//        this.listenner.clear();
        closeKeyboard();
        this.friends.clear();
        loadContacts();

        for(ContactsFragment.ReloadContacts reload : reloadChildContacts){
            reload.onReloadContacts();
        }

//        adapter = new ContactPagerAdapter(getChildFragmentManager());
//        viewPager.setAdapter(adapter);

    }

    public interface ReloadAllContactsFragments{
        void onReloadAllContactsFragments();
    }

    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
