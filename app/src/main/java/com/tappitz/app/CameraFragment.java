package com.tappitz.app;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tappitz.app.camera.CameraPreview4;


/**
 * Fragment for displaying the camera preview.
 *
 * @author Sebastian Kaspari <sebastian@androidzeitgeist.com>
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback {
    public static final String TAG = "Mustache/CameraFragment";

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private int cameraId;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    CameraPreview4 previewView;
    FrameLayout frame;
    /**
     * On activity getting attached.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    /**
     * On creating view for fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        //previewView.getHolder().addCallback(this);
        frame =  (FrameLayout)rootView.findViewById(R.id.frame);

        return rootView;
    }

    /**
     * On fragment getting resumed.
     */
    @Override
    public void onResume() {
        super.onResume();

        try {


            //previewView = new CameraPreview4(getActivity());
            frame.addView(previewView);


            camera = Camera.open(cameraId);
            determineDisplayOrientation();
            previewView.setCamera(camera);
        } catch (Exception exception) {
            Log.e(TAG, "Can't open camera with id " + cameraId, exception);

            return;
        }

        Pair<Integer, Integer> size = getScreenSize();
        Log.d(TAG, "width2------->:"+size.first + " height2:"+size.second);
    }

    /**
     * On fragment getting paused.
     */
    @Override
    public void onPause() {
        super.onPause();

//        stopCameraPreview();
//        camera.release();
        if (camera != null) {
            previewView.setCamera(null);
            camera.release();
            camera = null;
        }
        frame.removeAllViews();
    }

    /**
     * Start the camera preview.
     */
    private synchronized void startCameraPreview() {
        determineDisplayOrientation();
        setupCamera();

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException exception) {
            Log.e(TAG, "Can't start camera preview due to IOException", exception);

        }
    }

    /**
     * Stop the camera preview.
     */
    private synchronized void stopCameraPreview() {
        try {
            camera.stopPreview();
        } catch (Exception exception) {
            Log.i(TAG, "Exception during stopping camera preview");
        }
    }

    /**
     * Determine the current display orientation and rotate the camera preview
     * accordingly.
     */
    public void determineDisplayOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
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

        camera.setDisplayOrientation(displayOrientation);
    }

    /**
     * Setup the camera parameters.
     */
    public void setupCamera() {
        Camera.Parameters parameters = camera.getParameters();

        Size bestPreviewSize = determineBestPreviewSize(parameters);
        Size bestPictureSize = determineBestPictureSize(parameters);

        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

        camera.setParameters(parameters);
    }

    private Size determineBestPreviewSize(Camera.Parameters parameters) {
        List<Size> sizes = parameters.getSupportedPreviewSizes();

        return determineBestSize(sizes, PREVIEW_SIZE_MAX_WIDTH);
    }

    private Size determineBestPictureSize(Camera.Parameters parameters) {
        List<Size> sizes = parameters.getSupportedPictureSizes();

        return determineBestSize(sizes, PICTURE_SIZE_MAX_WIDTH);
    }

    protected Size determineBestSize(List<Size> sizes, int widthThreshold) {
        Size bestSize = null;

        for (Size currentSize : sizes) {
            boolean isDesiredRatio = (currentSize.width / 16) == (currentSize.height / 9);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;

            if (isDesiredRatio && isInBounds && isBetterSize) {
                bestSize = currentSize;
            }
        }

        if (bestSize == null) {

            return sizes.get(0);
        }

        return bestSize;
    }

    private Pair<Integer, Integer> getScreenSize(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();

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

    /**
     * On camera preview surface created.
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;

        startCameraPreview();
    }

    /**
     * On camera preview surface changed.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // The interface forces us to have this method but we don't need it
        // up to now.
    }

    /**
     * On camera preview surface getting destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // We don't need to handle this case as the fragment takes care of
        // releasing the camera when needed.
    }
}