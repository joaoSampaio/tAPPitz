package com.tappitz.tappitz.ui;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.util.List;


public class MiddleContainerFragment extends Fragment {

    View rootView;
    private MiddlePagerAdapter adapter;
    private VerticalViewPager viewPager;
    private List<ListenerPagerStateChange> stateChange;
    private int totalPages = 2;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer[] colors = null;
    public MiddleContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_middle_container, container, false);

        Log.d("myapp2", "**--new InBoxFragment:");

        adapter = new MiddlePagerAdapter(getChildFragmentManager());
        viewPager = (VerticalViewPager) rootView.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(adapter);
//        viewPager.setPageTransformer(false, new FadePageTransformer());

        setUpColors();


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener () {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (totalPages -1) && position < (colors.length - 1)) {

                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));

                } else {

                    // the last page color
                    viewPager.setBackgroundColor(colors[colors.length - 1]);

                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop(){
        super.onStop();
    }


    private void setUpColors(){

        Integer color1 = getResources().getColor(R.color.bg_transparent);
        Integer color2 = getResources().getColor(R.color.bg_middle);

        Integer[] colors_temp = {color1, color2};
        colors = colors_temp;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inbox, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public VerticalViewPager getViewPager() {
        return viewPager;
    }



    private class MiddlePagerAdapter extends FragmentStatePagerAdapter {

        public MiddlePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){

                case 0:
                    fragment = new BlankFragment();
                    break;
                case 1:
//                    fragment = new ContactsFragment();
                    fragment = new Contact_Settings_ContainerFragment();
                    break;

            }

            return fragment;
        }

        @Override
        public int getCount() {
            return totalPages;
        }

    }

    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {

            if(position <= -1.0F || position >= 1.0F) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setTranslationX(view.getWidth() * -position);
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }

}
