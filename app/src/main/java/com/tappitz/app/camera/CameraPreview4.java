package com.tappitz.app.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tappitz.app.CameraActivity;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.app.AppController;

import java.io.IOException;
import java.util.List;

/**
 * Created by Sampaio on 05/05/2016.
 */
public class CameraPreview4 extends ViewGroup implements SurfaceHolder.Callback, CameraInterface{
    private static final double ASPECT_RATIO = 9.0 / 16.0;
    private final String TAG = "Preview";
    SurfaceView mSurfaceView;
    boolean isCreated = false;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    Activity activity;

//    public CameraPreview4(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.activity = ((CameraActivity)getContext());
//        mSurfaceView = new SurfaceView(activity);
//        addView(mSurfaceView);
//        // Install a SurfaceHolder.Callback so we get notified when the
//        // underlying surface is created and destroyed.
//        mHolder = mSurfaceView.getHolder();
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    }

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
            requestLayout();
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(mHolder);
                    surfaceChangedCamera();
                }
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("MyCameraApp", "onMeasure:"+ (mSupportedPreviewSizes != null));
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


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "onLayout surfaceCreated:" + (mCamera != null));
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }else{

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       startCamera();
                   }
               },200);



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
            mCamera.release();
            mCamera = null;
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

    private boolean reachedSurfaceChanged = false;

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Log.d(TAG, " surfaceChanged)");
        reachedSurfaceChanged = true;
        if(mCamera != null){
            surfaceChangedCamera();
        }

    }


    private void surfaceChangedCamera(){
        Camera.Parameters parameters = mCamera.getParameters();
        if (mPreviewSize == null) {
            Pair<Integer, Integer> sizes = getScreenSize();
            int width = sizes.first;
            int height = sizes.second;
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    public Camera getmCamera() {
        return mCamera;
    }

    public void startCamera(){
        Log.d("MyCameraApp", "start camera .....>>>>>:");




        try {
            mCamera = Camera.open(AppController.getInstance().currentCameraId);
            determineDisplayOrientation();
            AppController.getInstance().mCameraReady = true;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            Log.d("MyCameraApp", "mSupportedPreviewSizes:"+mSupportedPreviewSizes.size());
            requestLayout();
            mCamera.setPreviewDisplay(mHolder);


            if(reachedSurfaceChanged)
                surfaceChangedCamera();

        } catch (Exception exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }




//        if(getActivity().getListenerCamera() != null)
//            getActivity().getListenerCamera().onCameraAvailable();
//        getActivity().notifyCameraReady();
//        btn_shutter.setVisibility(View.VISIBLE);
//        showBtnOptions(true);
//        onTakePick(false);
    }


    public void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(AppController.getInstance().currentCameraId, cameraInfo);

        int rotation = ((CameraActivity)getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int degrees  = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int displayOrientation;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(displayOrientation);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.set("jpeg-quality", 90);


//        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
//            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
//            case Surface.ROTATION_180: degrees = 180; break;//Upside down
//            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
//        }
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        int rotate = (info.orientation - degrees + 360) % 360;
        displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        parameters.setRotation(displayOrientation);

        mCamera.setParameters(parameters);
    }

    public void stopCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    public void restartPreview(){
        if(getmCamera() != null ){

            getmCamera().startPreview();

        }
    }


    @Override
    public void takePhoto(Camera.PictureCallback mPicture, CallbackCameraAction callback) {
        if(mCamera != null) {
            getmCamera().takePicture(null, null, mPicture);
            callback.onSuccess();
        }
    }

    @Override
    public void turnCamera(CallbackCameraAction callback) {
        Log.d("MyCameraApp", "turnCamera:");
        try {
            if(mCamera != null) {
                if(AppController.getInstance().currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                stopCamera();

                startCamera();

//                mCamera = Camera
//                        .open(AppController.getInstance().currentCameraId);
//                setCamera(mCamera);

                callback.onSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    @Override
    public void toggleFlash(CallbackCameraAction callback){

        try {
            AppController.getInstance().turnLightOn = !AppController.getInstance().turnLightOn;
            Camera.Parameters params = getmCamera().getParameters();

            if (AppController.getInstance().turnLightOn) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                getmCamera().setParameters(params);
                getmCamera().startPreview();
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                getmCamera().setParameters(params);
                getmCamera().startPreview();
            }
            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }










}