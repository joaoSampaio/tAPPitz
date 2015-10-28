package com.tappitz.tappitz.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sampaio on 27-10-2015.
 */
public class InfiniteViewPagerOld extends ViewPager {

    //float lastX = 0;

    boolean lockScroll = false;

    /** the last x position */
    private float   lastX;

    /** if the first swipe was from left to right (->), dont listen to swipes from the right */
    private boolean slidingLeft;

    /** if the first swipe was from right to left (<-), dont listen to swipes from the left */
    private boolean slidingRight;

    public InfiniteViewPagerOld(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfiniteViewPagerOld(Context context) {
        super(context);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = ev.getX();
//                lockScroll = false;
//                //return super.onTouchEvent(ev);
//                return true;
//            case MotionEvent.ACTION_MOVE:
//
//                if (lastX > ev.getX()) {
//                    lockScroll = true;
//                } else {
//                    lockScroll = false;
//                }
//
//                lastX = ev.getX();
//                break;
//        }
//
//        lastX = ev.getX();
//
//        if(lockScroll) {
//            return false;
//        } else {
//            //return super.onTouchEvent(ev);
//            return true;
//        }
//
//    }


    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                // Disallow parent ViewPager to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);

                // save the current x position
                this.lastX = ev.getX();

                break;

            case MotionEvent.ACTION_UP:
                // Allow parent ViewPager to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);

                // save the current x position
                this.lastX = ev.getX();

                // reset swipe actions
                this.slidingLeft = false;
                this.slidingRight = false;

                break;

            case MotionEvent.ACTION_MOVE:
                /*
                 * if this is the first item, scrolling from left to
                 * right should navigate in the surrounding ViewPager
                 */
                if (this.getCurrentItem() == 0) {
                    // swiping from left to right (->)?
                    if (this.lastX <= ev.getX() && !this.slidingRight) {
                        // make the parent touch interception active -> parent pager can swipe
                        this.getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        /*
                         * if the first swipe was from right to left, dont listen to swipes
                         * from left to right. this fixes glitches where the user first swipes
                         * right, then left and the scrolling state gets reset
                         */
                        this.slidingRight = true;

                        // save the current x position
                        this.lastX = ev.getX();
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else
                /*
                 * if this is the last item, scrolling from right to
                 * left should navigate in the surrounding ViewPager
                 */
                    if (this.getCurrentItem() == this.getAdapter().getCount() - 1) {
                        // swiping from right to left (<-)?
                        if (this.lastX >= ev.getX() && !this.slidingLeft) {
                            // make the parent touch interception active -> parent pager can swipe
                            this.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                        /*
                         * if the first swipe was from left to right, dont listen to swipes
                         * from right to left. this fixes glitches where the user first swipes
                         * left, then right and the scrolling state gets reset
                         */
                            this.slidingLeft = true;

                            // save the current x position
                            this.lastX = ev.getX();
                            this.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }




                break;
        }

        super.onTouchEvent(ev);
        return true;
    }



//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        // Never allow swiping to switch between pages
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = ev.getX();
//                lockScroll = false;
//                return super.onTouchEvent(ev);
//            case MotionEvent.ACTION_MOVE:
//
//                if (lastX > ev.getX()) {
//                    lockScroll = true;
//                } else {
//                    lockScroll = false;
//                }
//
//                lastX = ev.getX();
//                break;
//        }
//
//        lastX = ev.getX();
//
//        if(lockScroll) {
//            return false;
//        } else {
//            return super.onTouchEvent(ev);
//            //return true;
//        }
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // Never allow swiping to switch between pages
//        return false;
//    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(v != this && v instanceof ViewPager) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }


}