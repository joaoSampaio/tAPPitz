package com.tappitz.tappitz.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.CircularFragmentPagerAdapter;


public class MessageInOutFragment extends Fragment {

    public final static int PAGES = 2;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    ViewPager viewPager;
    View rootView;
    CircularFragmentPagerAdapter adapter;
    public MessageInOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message_in_out, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPagerMessage);
//
        adapter = new CircularFragmentPagerAdapter(getChildFragmentManager());
//        adapter.setInBox(new InBoxFragment());
//        adapter.setOutBox(new OutBoxFragment());
        adapter.setTotalpages(PAGES*LOOPS);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(adapter);

        viewPager.setCurrentItem(PAGES * LOOPS);








//        adapter.setTotalpages(2);
//        // wrap pager to provide a minimum of 4 pages
//        MinFragmentPagerAdapter wrappedMinAdapter = new MinFragmentPagerAdapter(getChildFragmentManager());
//        wrappedMinAdapter.setAdapter(adapter);
//
//        // wrap pager to provide infinite paging with wrap-around
//        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(wrappedMinAdapter);
//
//        // actually an InfiniteViewPager
//        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPagerMessage);
//        viewPager.setAdapter(wrappedAdapter);


//        adapter.setTotalpages(4);
//// wrap pager to provide infinite paging with wrap-around
//        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);
//
//        // actually an InfiniteViewPager
//        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPagerMessage);
//        viewPager.setAdapter(wrappedAdapter);



        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(viewPager != null){
            //inicializo no onstart porque se fosse ao fragmentos contactos, tinha problemas



//
//            adapter.setTotalpages(PAGES * LOOPS);
//            adapter.notifyDataSetChanged();
        }

    }



}
