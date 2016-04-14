package com.tappitz.tappitz.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.ui.secondary.InOutBoxOptionsFragment;
import com.tappitz.tappitz.ui.MiddleContainerFragment;
import com.tappitz.tappitz.ui.OutBoxFragment;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args;
        switch (position){

            case Global.INBOX:
                fragment = new InBoxFragment();
                break;
            case Global.HOME:
//                    fragment = new HomeFragment();
                //fragment = new BlankFragment();
                fragment = new MiddleContainerFragment();
                break;
            case Global.OUTBOX:
                fragment = new OutBoxFragment();
                break;
            case Global.INBOX_OP:
                fragment = new InOutBoxOptionsFragment();
                args = new Bundle();
                args.putString(Global.OPTIONS_TITLE, "Received");
                args.putInt(Global.OPTIONS_TYPE, Global.OPTIONS_TYPE_INBOX);
                fragment.setArguments(args);
                break;
            case Global.OUTBOX_OP:
                fragment = new InOutBoxOptionsFragment();
                args = new Bundle();
                args.putString(Global.OPTIONS_TITLE, "Sent");
                args.putInt(Global.OPTIONS_TYPE, Global.OPTIONS_TYPE_OUTBOX);
                fragment.setArguments(args);
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public float getPageWidth(int position) {
        float width = 1.0f;
        switch (position){

            case Global.INBOX_OP:
                width = 0.5f;
                break;
            case Global.OUTBOX_OP:
                width = 0.5f;
                break;
        }
        return(width);
    }

}
