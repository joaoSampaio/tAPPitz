package com.tappitz.tappitz.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.ui.MessageInOutFragment;
import com.tappitz.tappitz.ui.OutBoxFragment;

/**
 * Created by sampaio on 27-10-2015.
 */
public class CircularFragmentPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

//    private MyLinearLayout cur = null;
//    private MyLinearLayout next = null;
//    private MainActivity context;

    private Fragment inBox;
    private Fragment outBox;

    private int currentPage;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private float scale;
    private int totalpages = MessageInOutFragment.PAGES * MessageInOutFragment.LOOPS;
    private int total = MessageInOutFragment.PAGES * MessageInOutFragment.LOOPS;

    public CircularFragmentPagerAdapter( FragmentManager fm) {
        super(fm);
        this.mFragmentManager = fm;
//        if (fm.getFragments() != null) {
//            fm.getFragments().clear();
//        }
        //mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void setTotalpages(int totalpages) {
        this.totalpages = totalpages;
    }

    @Override
    public Fragment getItem(int position) {

        position = position % MessageInOutFragment.PAGES;
        Log.d("myapp2", "**--getItem real:" + position);
//        Fragment fragment = null;
//        switch (position){
//            case 0:
//                return new InBoxFragment();
//            case 1:
//                return new OutBoxFragment();
//            case 2:
//                return new InBoxFragment();
//            case 3:
//                return new OutBoxFragment();
//
//        }
//        return fragment;
        if(position == 0) {
//            if(inBox == null) {
//                Log.d("myapp2", "**inBox is null:" );
//                inBox = new InBoxFragment();
//                inBox.setRetainInstance(true);
//            }
            return new InBoxFragment();
        }else{
//            if(outBox == null) {
//                Log.d("myapp2", "**outBox is null:" );
//                outBox = new OutBoxFragment();
//                outBox.setRetainInstance(true);
//            }
            return new OutBoxFragment();

        }
    }

    @Override
    public int getCount() {
        return totalpages;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("myapp2", "**--onPageSelected:" + position);
        Log.d("myapp2", "**--onPageSelected totalpages:" + totalpages);
        currentPage = position;
        //if(totalpages > (position+2))

        if(totalpages == total && position < (total-2))
            return;
        if(position < (total-1))
        totalpages--;
        notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        if (mCurTransaction == null) {
//            mCurTransaction = mFragmentManager.beginTransaction();
//        }
//
//        Log.d("myapp2", "**--instantiateItem:" + container.getId());
//        int realPosition = position % MessageInOutFragment.PAGES;
//        //final long itemId = getItemId(position);
//        // Do we already have this fragment?
//        String name = "inout" + realPosition;
//        //String name = makeFragmentName(container.getId(), itemId);
//        Fragment fragment = mFragmentManager.findFragmentByTag(name);
//        if (fragment != null) {
//            Log.d("myapp2", "**--fragment != null:" + position);
//            //mCurTransaction.attach(fragment);
//            mCurTransaction.show(fragment);
//
//
////            mCurTransaction.remove(fragment);
////            fragment = getItem(position);
////
////            mCurTransaction.commitAllowingStateLoss();
////            mCurTransaction = null;
////            mFragmentManager.executePendingTransactions();
////
////
////            mCurTransaction = mFragmentManager.beginTransaction();
////            mCurTransaction.add(container.getId(), fragment, name);
//
//        } else {
//            Log.d("myapp2", "**--else:" + position);
//            fragment = getItem(position);
//            mCurTransaction.add(container.getId(), fragment, name);
//        }
//
//        return fragment;
//    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        int virtualPosition = position % getRealCount();
//        debug("destroyItem: real position: " + position);
//        debug("destroyItem: virtual position: " + virtualPosition);
//
//        // only expose virtual position to the inner adapter
//        adapter.destroyItem(container, virtualPosition, object);
//
//        if (getRealCount() < 4) {
//            adapter.instantiateItem(container, virtualPosition);
//            return;
//        }
//    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        if (mCurTransaction == null) {
//            mCurTransaction = mFragmentManager.beginTransaction();
//        }
//        Log.v("myapp2", "Detaching item #" + getItemId(position) + ": f=" + object
//                + " v=" + ((Fragment) object).getView());
//        //mCurTransaction.detach((Fragment) object);
////        int before = currentPage - 2;
////        int after = currentPage - 2;
////        if(!(position == before || position == after))
////            mCurTransaction.hide((Fragment)object);
//        mCurTransaction.detach((Fragment)object);
//    }

//    @Override
//    public void finishUpdate(ViewGroup container) {
//        if (mCurTransaction != null) {
//            //mCurTransaction.commit();
//            mCurTransaction.commitAllowingStateLoss();
//            mCurTransaction = null;
//            mFragmentManager.executePendingTransactions();
//        }
//    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }


    public void setInBox(Fragment inBox) {
        this.inBox = inBox;
    }

    public void setOutBox(Fragment outBox) {
        this.outBox = outBox;
    }
}