package com.tappitz.app.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.tappitz.app.ui.secondary.ContactsFragment;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class ContactPagerAdapter extends FragmentStatePagerAdapter {

    public ContactPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args;
        Log.d("ContactsFragment", "ContactPagerAdapter: "+position);
        switch (position){

            case 0:
                fragment = ContactsFragment.newInstance(ContactsFragment.FOLLOWING);
                break;
            case 1:
                fragment = ContactsFragment.newInstance(ContactsFragment.FOLLOWERS);
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }



}
