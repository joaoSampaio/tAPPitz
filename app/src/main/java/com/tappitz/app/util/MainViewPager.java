package com.tappitz.app.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MainViewPager extends ViewPager {
    private boolean enabled;
    private View mCurrentView;

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (mCurrentView == null) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            return;
//        }
//        int height = 0;
//        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//        int h = mCurrentView.getMeasuredHeight();
//        if (h > height) height = h;
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    public void measureCurrentView(View currentView) {
//        mCurrentView = currentView;
//        requestLayout();
//    }

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        super.setPrimaryItem(container, position, object);
//        if (position != mCurrentPosition) {
//            Fragment fragment = (Fragment) object;
//            MainViewPager pager = (MainViewPager) container;
//            if (fragment != null && fragment.getView() != null) {
//                mCurrentPosition = position;
//                pager.measureCurrentView(fragment.getView());
//            }
//        }
//    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    /**
     * Custom implementation to enable or not swipe :)
     *
     * @param enabled
     *            true to enable swipe, false otherwise.
     */
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
