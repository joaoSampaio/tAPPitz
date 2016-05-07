package com.tappitz.app.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by Sampaio on 05/05/2016.
 */
public class CameraPreview3 extends SurfaceView {
    private static final double ASPECT_RATIO = 9.0 / 16.0;

    public CameraPreview3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CameraPreview3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreview3(Context context) {
        super(context);
    }

    /**
     * Measure the view and its content to determine the measured width and the
     * measured height.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (width > height * ASPECT_RATIO) {
            width = (int) (height * ASPECT_RATIO + .5);
        } else {
            height = (int) (width / ASPECT_RATIO + .5);
        }

        setMeasuredDimension(width, height);
    }
}