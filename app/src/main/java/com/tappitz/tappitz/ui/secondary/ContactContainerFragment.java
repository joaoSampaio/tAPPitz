package com.tappitz.tappitz.ui.secondary;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ContactPagerAdapter;

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
    public ContactContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact_container, container, false);
        searchListener = new ArrayList<>();
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
    }


    @Override
    public void onPause(){
        super.onPause();
        searchListener.clear();
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

}
