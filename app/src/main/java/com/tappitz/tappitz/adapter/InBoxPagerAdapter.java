package com.tappitz.tappitz.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.ui.secondary.InBoxPageFragment;

import java.util.List;


public class InBoxPagerAdapter extends FragmentStatePagerAdapter {

    private List<ReceivedPhoto> photos;
    public InBoxPagerAdapter(FragmentManager fm, List<ReceivedPhoto> photos) {
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
        args.putString(Global.IMAGE_RESOURCE_URL, photos.get(position).getUrl());
        args.putString(Global.TEXT_RESOURCE, photos.get(position).getPictureSentence());
        args.putInt(Global.ID_RESOURCE, photos.get(position).getPictureId());
        args.putString(Global.OWNER_RESOURCE, photos.get(position).getAuthorName());
        args.putString(Global.DATE_RESOURCE, photos.get(position).getTimeAgo(photos.get(position).getSentDate()));
        if(photos.get(position).isHasVoted())
            args.putString(Global.VOTE_DATE_RESOURCE, photos.get(position).getTimeAgo(photos.get(position).getVotedDate()));
        args.putString(Global.MYCOMMENT_RESOURCE, photos.get(position).getComment());

        args.putBoolean(Global.HAS_VOTED_RESOURCE, photos.get(position).isHasVoted());
        args.putInt(Global.CHOICE_RESOURCE, photos.get(position).getVote());
        args.putBoolean(Global.IS_TEMPORARY_RESOURCE, photos.get(position).isVoteTemporary());



        return InBoxPageFragment.newInstance(args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Child Fragment " + position;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
//    }
}
