package com.tappitz.tappitz.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.ui.secondary.OutBoxPageFragment;

import java.util.List;


public class OutBoxPagerAdapter extends FragmentStatePagerAdapter {

    private List<PhotoOutbox> photos;
    public OutBoxPagerAdapter(FragmentManager fm, List<PhotoOutbox> photos) {
        super(fm);
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        //args.putInt(OutBoxPageFragment.POSITION_KEY, position);
        args.putString(Global.IMAGE_RESOURCE_URL, photos.get(position).getUrl());
        args.putString(Global.TEXT_RESOURCE, photos.get(position).getText());
        args.putString(Global.TEXT_RESOURCE, photos.get(position).getText());
        args.putInt(Global.ID_RESOURCE, photos.get(position).getId());

        return OutBoxPageFragment.newInstance(args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Child Fragment " + position;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
