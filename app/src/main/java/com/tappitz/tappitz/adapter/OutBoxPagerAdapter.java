package com.tappitz.tappitz.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.model.photo_tAPPitz;
import com.tappitz.tappitz.ui.secondary.OutBoxPageFragment;

import java.util.List;


public class OutBoxPagerAdapter extends FragmentPagerAdapter {

    private List<photo_tAPPitz> photos;
    public OutBoxPagerAdapter(FragmentManager fm, List<photo_tAPPitz> photos) {
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
        args.putString(Global.ID_RESOURCE, photos.get(position).getId());

        return OutBoxPageFragment.newInstance(args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Child Fragment " + position;
    }
}
