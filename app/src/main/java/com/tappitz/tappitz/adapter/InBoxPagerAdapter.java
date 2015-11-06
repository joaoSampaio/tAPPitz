package com.tappitz.tappitz.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.ui.secondary.InBoxPageFragment;

import java.util.List;


public class InBoxPagerAdapter extends FragmentPagerAdapter {

    private List<PhotoInbox> photos;
    public InBoxPagerAdapter(FragmentManager fm, List<PhotoInbox> photos) {
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
        args.putString(Global.ID_RESOURCE, photos.get(position).getId());
        args.putString(Global.OWNER_RESOURCE, photos.get(position).getSenderName());
        args.putString(Global.DATE_RESOURCE, photos.get(position).getDate());
        args.putString(Global.MYCOMMENT_RESOURCE, photos.get(position).getMyComment());

        args.putBoolean(Global.HAS_VOTED_RESOURCE, photos.get(position).isHasVoted());
        args.putInt(Global.CHOICE_RESOURCE, photos.get(position).getChoice());

        return InBoxPageFragment.newInstance(args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Child Fragment " + position;
    }
}
