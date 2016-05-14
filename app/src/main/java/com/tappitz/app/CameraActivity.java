package com.tappitz.app;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tappitz.app.camera.CameraPreview4;


/**
 * Activity displaying the camera and mustache preview.
 *
 * @author Sebastian Kaspari <sebastian@androidzeitgeist.com>
 */
public class CameraActivity extends Activity {
    public static final String TAG = "CameraActivity";

    CameraPreview4 previewView;
    FrameLayout frame;
    private Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        frame = (FrameLayout)findViewById(R.id.camera);


    }

    @Override
    public void onResume() {
        super.onResume();

        try {


            previewView = new CameraPreview4(this);
            frame.addView(previewView);


//            camera = Camera.open(1);
//            determineDisplayOrientation();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    previewView.setCamera(camera);
//                }
//            },1000);
//            previewView.setCamera(camera);
        } catch (Exception exception) {
            Log.e(TAG, "Can't open camera with id ", exception);

            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

//        stopCameraPreview();
//        camera.release();
//        if (camera != null) {
//            previewView.setCamera(null);
//            camera.release();
//            camera = null;
//        }
        frame.removeAllViews();
    }

    public void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(1, cameraInfo);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
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

}