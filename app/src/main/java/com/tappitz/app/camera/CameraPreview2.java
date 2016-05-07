package com.tappitz.app.camera;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.tappitz.app.R;
import com.tappitz.app.ui.ScreenSlidePagerActivity;

import java.io.IOException;
import java.util.List;

/**
 * Created by joaosampaio on 12-02-2016.
 */
public class CameraPreview2 implements TextureView.SurfaceTextureListener {
    private SurfaceHolder mHolder;
    String TAG = "cameraApp";
    private static final double ASPECT_RATIO = 3.0 / 4.0;
    private ScreenSlidePagerActivity activity;
    private int orgPreviewWidth;
    private int orgPreviewHeight;
    private Camera mCamera;
    private TextureView mTextureView;

    public CameraPreview2(ScreenSlidePagerActivity activity) {
        this.activity = activity;
        //mTextureView = (TextureView) activity.findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);


    }






    private void updateTextureMatrix(int width, int height)
    {
        boolean isPortrait = false;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) isPortrait = true;
        else if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) isPortrait = false;

        int previewWidth = orgPreviewWidth;
        int previewHeight = orgPreviewHeight;

        if (isPortrait)
        {
            previewWidth = orgPreviewHeight;
            previewHeight = orgPreviewWidth;
        }

        float ratioSurface = (float) width / height;
        float ratioPreview = (float) previewWidth / previewHeight;

        float scaleX;
        float scaleY;

        if (ratioSurface > ratioPreview)
        {
            scaleX = (float) height / previewHeight;
            scaleY = 1;
        }
        else
        {
            scaleX = 1;
            scaleY = (float) width / previewWidth;
        }

        Matrix matrix = new Matrix();

        matrix.setScale(scaleX, scaleY);
        mTextureView.setTransform(matrix);

        float scaledWidth = width * scaleX;
        float scaledHeight = height * scaleY;

        float dx = (width - scaledWidth) / 2;
        float dy = (height - scaledHeight) / 2;
        mTextureView.setTranslationX(dx);
        mTextureView.setTranslationY(dy);
    }


    public Camera getmCamera() {
        return mCamera;
    }

    public ScreenSlidePagerActivity getActivity() {
        return activity;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {


        try
        {
            mCamera = Camera.open(0);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();


            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            Pair<Integer, Integer> size = getMaxSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(size.first, size.second);

            orgPreviewWidth = size.first;
            orgPreviewHeight = size.second;

            mCamera.setParameters(parameters);
            setCameraDisplayOrientation(0, mCamera);

            updateTextureMatrix(orgPreviewWidth, orgPreviewHeight);

        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        try {
            if(mCamera!=null){
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);

                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error surfaceDestroyed: " + e.getMessage());
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    private Pair<Integer, Integer> getMaxSize(List<Camera.Size> sizes)
    {
        int width = 0;
        int height = 0;

        int maxHeight = getScreenSize().second;

        Camera.Size sizeScreen = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {

            if (sizes.get(i).width > sizeScreen.width)
                sizeScreen = sizes.get(i);
            if(sizes.get(i).height == maxHeight) {
                sizeScreen = sizes.get(i);
                break;
            }
        }

        width = sizeScreen.width;
        height = sizeScreen.height;

        return new Pair<Integer, Integer>(width, height);
    }


    private Pair<Integer, Integer> getScreenSize(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new Pair<Integer, Integer>(width, height);
    }

    public  void setCameraDisplayOrientation( int cameraId, Camera camera)
    {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
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

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else
        {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        Camera.Parameters params = camera.getParameters();
        params.setRotation(result);
        camera.setParameters(params);
    }



}