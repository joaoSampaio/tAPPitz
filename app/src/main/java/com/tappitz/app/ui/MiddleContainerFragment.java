package com.tappitz.app.ui;

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

import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.ui.secondary.ContactContainerFragment;
import com.tappitz.app.ui.secondary.QRCodeFragment;
import com.tappitz.app.util.ListenerPagerStateChange;
import com.tappitz.app.util.VerticalViewPager;

import java.util.List;


public class MiddleContainerFragment extends Fragment implements ViewPager.OnPageChangeListener {

    View rootView;
    private MiddlePagerAdapter adapter;
    private VerticalViewPager viewPager;
    private List<ListenerPagerStateChange> stateChange;
    private int totalPages = 3;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer[] colors = null;

    private int positionTab = 0;
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
        viewPager.setCurrentItem(1);
        viewPager.setPageTransformer(true, new CustomPageTransformer());
        setUpColors();

        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener () {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(position < (totalPages -1) && position < (colors.length - 1)) {
//
//                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
//
//                } else {
//
//                    // the last page color
//                    viewPager.setBackgroundColor(colors[colors.length - 1]);
//
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//
//        });

        ((ScreenSlidePagerActivity)getActivity()).setMiddleShowPage(new MiddleShowPage() {
            @Override
            public void showPage(int page) {
                Log.d("MIddle", "showPage:" + page);

                viewPager.setPageTransformer(true, null);
//                viewPager.setOnPageChangeListener(null);
                showPageMiddle(page);
                viewPager.setPageTransformer(true, new CustomPageTransformer());
//                setPageChangeListener();
            }
        });


        return rootView;
    }

    private void setPageChangeListener(){
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop(){
        super.onStop();
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.d("ss", "positionTab:" + position);
        positionTab = position;
        if(position >= 1 && position < (totalPages -1) && position < (colors.length - 1)) {

            viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));

        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("ss", "onPageSelected:" + position);
        if(getActivity() != null){
            ((ScreenSlidePagerActivity)getActivity()).enableQRCodeCapture((position == Global.MIDDLE_QRCODE));
            if(((ScreenSlidePagerActivity)getActivity()).getmPager() != null)
                ((ScreenSlidePagerActivity)getActivity()).getmPager().setPagingEnabled(position == Global.MIDDLE_BLANK);

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }



    private void setUpColors(){

        Integer color1 = getResources().getColor(R.color.bg_transparent);

        Integer color2 = getResources().getColor(R.color.bg_middle);

        Integer[] colors_temp = {color1, color1, color2};
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

                case Global.MIDDLE_QRCODE:
                    fragment = new QRCodeFragment();
                    break;

                case Global.MIDDLE_BLANK:
                    fragment = new BlankFragment();
                    break;
                case Global.MIDDLE_CONTACTS:
                    fragment = new ContactContainerFragment();
//                    fragment = new Contact_Settings_ContainerFragment();
                    break;

            }

            return fragment;
        }

        @Override
        public int getCount() {
            return totalPages;
        }

    }

    public class CustomPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (positionTab < 2 && positionTab >= 0) {
                View container = view.findViewById(R.id.container);

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left
                } else if (position <= 0) { // [-1,0]
                    // This page is moving out to the left

                    // Counteract the default swipe
                    //view.setTranslationX(pageWidth * -position);

                    if (container != null) {
//                        Log.d("myapp2", "**--Fade the image in:");
                        // Fade the image in
                        container.setAlpha(1 + position);
                        if(getActivity() != null)
                            ((ScreenSlidePagerActivity)getActivity()).fadeCameraBts(1 - position);
                    }

                } else if (position <= 1) { // (0,1]
                    // This page is moving in from the right
                    if (container != null) {
                        // Fade the image out
//                        Log.d("myapp2", "**--Fade the image out:");
                        container.setAlpha(1 - position);
                        if(getActivity() != null)
                            ((ScreenSlidePagerActivity)getActivity()).fadeCameraBts(1 + position);
                    }
                } else { // (1,+Infinity]
                    // This page is way off-screen to the right
                }
            }
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

    public void showPageMiddle(int page){
        if(adapter != null && page >= 0 && page < 3){
            viewPager.setCurrentItem(page);
        }
    }

    public interface MiddleShowPage{
        void showPage(int page);
    }

}
