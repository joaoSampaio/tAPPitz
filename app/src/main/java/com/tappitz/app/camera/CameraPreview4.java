package com.tappitz.app.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.List;

/**
 * Created by Sampaio on 05/05/2016.
 */
public class CameraPreview4 extends ViewGroup implements SurfaceHolder.Callback{
    private static final double ASPECT_RATIO = 9.0 / 16.0;
    private final String TAG = "Preview";
    SurfaceView mSurfaceView;
    boolean isCreated = false;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    Activity activity;



    public CameraPreview4( Activity activity) {
        super(activity);
        this.activity = activity;
        mSurfaceView = new SurfaceView(activity);
        addView(mSurfaceView);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
        Log.d(TAG, "mCamera:"+(mCamera != null));
        Log.d(TAG, "isCreated:"+(isCreated));
        if(isCreated){
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();
                }
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
//        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        Pair<Integer, Integer> sizes = getScreenSize();
        int width = sizes.first;
        int height = sizes.second;



                setMeasuredDimension(width, height);
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    private Pair<Integer, Integer> getScreenSize(){
        Display display = activity.getWindowManager().getDefaultDisplay();

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height =  height + resources.getDimensionPixelSize(resourceId);
        }

        return new Pair<Integer, Integer>(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

             int width = r - l;
             int height = b - t;





            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }
            Log.d(TAG, "onLayout previewWidth:"+previewWidth + " preview previewHeight:"+previewHeight);
            if(previewWidth > previewHeight) {
                int tmpWidth = previewWidth;
                previewWidth = previewHeight;
                previewHeight = tmpWidth;
            }
            Log.d(TAG, "onLayout previewWidth:"+previewWidth + " preview previewHeight:"+previewHeight);
            // Center the child SurfaceView within the parent.
            if (width * previewHeight < height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }


    public void setCameraLayout(int width, int height) {
        float newProportion = (float) width / (float) height;
        // Get the width of the screen
        Point point = new Point();
        this.activity.getWindowManager().getDefaultDisplay().getSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        float screenProportion = (float) screenWidth / (float) screenHeight;
        // Get the SurfaceView layout parameters
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this
                .getLayoutParams();
        float scaleFactor = 1;
        /*
         * assume width is smaller than height in screen and in input
		 * parameters. Therefore if newProportion > screenProportion then
		 * The desire proportion is more wider than higher therefore we match it against
		 * screen width and scale it height with the new proportion
		 *
		 */
        if (newProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth * (1 / newProportion));
            scaleFactor = (screenHeight / lp.height); // calculate the factor to make it full screen
        } else {
            lp.width = (int) (newProportion * (float) screenHeight);
            lp.height = screenHeight;
            scaleFactor = screenWidth / lp.width; // calculate the factor to make it full screen.

        }
        lp.width = (int) (lp.width * scaleFactor);
        lp.height = (int) (lp.height * scaleFactor);
        lp.gravity = Gravity.CENTER;
        mSurfaceView.setLayoutParams(lp);


    }


    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        isCreated = true;
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        Log.d(TAG, "w:"+w + " h:"+h);
        if(h > w){
            int tmpWidth = w;
            w = h;
            h = tmpWidth;
        }
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        Log.d(TAG, "targetRatio:"+targetRatio );
        if (sizes == null) return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d(TAG, "width:"+optimalSize.width + " height:"+optimalSize.height);
        return optimalSize;
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


}