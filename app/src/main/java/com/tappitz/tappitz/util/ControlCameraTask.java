package com.tappitz.tappitz.util;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import com.tappitz.tappitz.app.AppController;

import java.util.List;

/**
 * Created by sampaio on 12-10-2015.
 */
public class ControlCameraTask extends AsyncTask<Boolean, Void, Void> {

    private CallbackCamera callback;
    private boolean error = false;
    public ControlCameraTask(){

    }

    public void setCallback(CallbackCamera callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        Log.d("MyCameraApp", "doInBackground");
        error = false;
        boolean requestOpen = params[0];
        AppController app = AppController.getInstance();
        if (!requestOpen && app.mCamera != null) {
            Log.d("myApp", "Cameratask stop");
            app.mCamera.stopPreview();
            app.mCamera.setPreviewCallback(null);
            app.mCamera.release();
            app.mCamera = null;
        }

        if(requestOpen){

            if(app.mCamera == null) {
                try {
//                    Thread.sleep(100);
                    Log.d("MyCameraApp", "Camera.open antes");
                    app.mCamera = Camera.open(app.currentCameraId);
                    Log.d("MyCameraApp", "Camera.open");
                    setCameraDisplayOrientation(app.currentCameraId, app.mCamera);
                    Log.d("MyCameraApp", "setCameraDisplayOrientation");
                    app.mCamera.setPreviewDisplay(app.surfaceHolder);
                    Log.d("MyCameraApp", "Camera.setPreviewDisplay");
                    app.mCamera.startPreview();
                    Log.d("MyCameraApp", "Camera.startPreview");
                } catch (Exception e) {
                    Log.e("myapp",  "erro2");
                    Log.e("myapp",  e.getMessage());
                    error = true;

                }
            }else{
                try {
                    Log.d("MyCameraApp", "preview");
                    app.mCamera.setPreviewDisplay(app.surfaceHolder);
                    app.mCamera.startPreview();
                    //previewing = true;
                } catch (Exception e) {
                    Log.e("myapp",  "erro3");
                    e.printStackTrace();
                    error = true;
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(callback != null ) {
            if( !error)
                callback.onDone();
            else
                callback.onError();
        }
    }



    public static void setCameraDisplayOrientation(
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        AppController app = AppController.getInstance();
        int degrees = 0;
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            //Choose another supported mode
        }

        //temos de trocar os valores caso a width seja mais baixa que o height
//        int maxWidth = app.width > app.height? app.width : app.height;
//        int maxHeight = app.width > app.height? app.height : app.width;
//
//
//
//        List<Camera.Size> sizes = sizes = parameters.getSupportedPreviewSizes();
//        Camera.Size sizeScreen = sizes.get(0);
//        for (int i = 0; i < sizes.size(); i++) {
//
//            if (sizes.get(i).width > sizeScreen.width)
//                sizeScreen = sizes.get(i);
//            if(sizes.get(i).height == maxHeight) {
//                sizeScreen = sizes.get(i);
//                break;
//            }
//        }
//        Log.d("MyCameraApp", "sizeScreen size.width: " + sizeScreen.width + " size.height: " + sizeScreen.height);
//        parameters.setPreviewSize(sizeScreen.width, sizeScreen.height);
//
//
//        sizes = parameters.getSupportedPictureSizes();
//        Camera.Size sizeCamera = sizes.get(0);
//        for (int i = 0; i < sizes.size(); i++) {
//            Log.d("myapp", "size.width: " +sizes.get(i).width + " size.height: " + +sizes.get(i).height);
//            if (sizes.get(i).width > sizeCamera.width)
//                sizeCamera = sizes.get(i);
//            if(sizes.get(i).height == maxHeight) {
//                sizeCamera = sizes.get(i);
//                break;
//            }
//        }
//
//
//        Log.d("MyCameraApp", "best size.width: " + sizeCamera.width + " size.height: " + sizeCamera.height);
//        parameters.setPictureSize(sizeCamera.width, sizeCamera.height);


        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.set("jpeg-quality", 90);
        parameters.setRotation(degrees);
        parameters.set("orientation", "portrait");
        degrees = 90;
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            degrees = 270;
        parameters.set("rotation", degrees);
        camera.setParameters(parameters);
    }


    public interface CallbackCamera {
        public void onDone();
        public void onError();
    }
}


