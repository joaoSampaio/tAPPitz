package com.tappitz.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tappitz.app.app.AppController;
import com.tappitz.app.camera.CallbackCameraAction;
import com.tappitz.app.camera.CameraPreview4;
import com.tappitz.app.camera.ControlCameraTask;
import com.tappitz.app.camera.SavePhotoBackgroundTask;
import com.tappitz.app.camera.UriPath;


/**
 * Activity displaying the camera and mustache preview.
 *
 * @author Sebastian Kaspari <sebastian@androidzeitgeist.com>
 */
public class CameraActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "CameraActivity";

    final static int[] CLICABLES = {R.id.camera_options, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept, R.id.btn_shutter};
    RelativeLayout layout_after_photo, layout_before_photo;
    CameraPreview4 previewView;
    FrameLayout frame;
    private Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        frame = (FrameLayout)findViewById(R.id.camera);

        for(int id: CLICABLES)
            findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {


            //previewView = new CameraPreview4(this);
            frame.addView(previewView);
            layout_after_photo = (RelativeLayout)findViewById(R.id.layout_after_photo);
            layout_before_photo = (RelativeLayout)findViewById(R.id.layout_before_photo);

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

//    public void determineDisplayOrientation() {
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        Camera.getCameraInfo(1, cameraInfo);
//
//        int rotation = getWindowManager().getDefaultDisplay().getRotation();
//        int degrees  = 0;
//
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//
//        int displayOrientation;
//
//        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            displayOrientation = (cameraInfo.orientation + degrees) % 360;
//            displayOrientation = (360 - displayOrientation) % 360;
//        } else {
//            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
//        }
//
//        camera.setDisplayOrientation(displayOrientation);
//    }



    @Override
    public void onClick(View v) {
        View mView;
        switch (v.getId()){
            case R.id.camera_options:

                mView = findViewById(R.id.layout_camera);
                mView.setVisibility(mView.isShown()? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_shutter:

                Log.d("myapp", "btn_shutter");
                previewView.takePhoto(mPicture, new CallbackCameraAction() {
                    @Override
                    public void onSuccess() {
//                        (activity).enableSwipe(false);
                        onTakePick(true);
//                        getActivity().screenHistory.add(0, 0);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("myapp", "onFailure");
                    }
                });
                break;

            case R.id.btnPhotoDelete:

                Log.d("myapp", "btnPhotoDelete");
                deletePhoto();

                break;

            case R.id.btn_flash:
                Log.d("myapp", "btn_flash:");
//                enableCameraButtons(false);
                previewView.toggleFlash(new CallbackCameraAction() {
                    @Override
                    public void onSuccess() {
                        Button b = (Button)findViewById(R.id.btn_flash);
                        b.setTextColor(AppController.getInstance().turnLightOn ? Color.YELLOW : Color.WHITE);
//                        enableCameraButtons(true);
                    }

                    @Override
                    public void onFailure() {
//                        enableCameraButtons(true);
                    }
                });

                break;
            case R.id.btn_toggle_camera:
                Log.d("myapp", "btn_toggle_camera:");

                PackageManager pm = getPackageManager();

                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
//                    enableCameraButtons(true);
                    Toast.makeText(this, "You only have one camera!", Toast.LENGTH_SHORT).show();
                    break;
                }
                previewView.turnCamera(new CallbackCameraAction() {
                    @Override
                    public void onSuccess() {
                        Log.d("myapp", "btn_toggle_camera onDone:");
//                        enableCameraButtons(true);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("myapp", "btn_toggle_camera onError:");
//                        enableCameraButtons(true);
                    }
                });

        }
    }


    public Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap



        }
    };

    public void deletePhoto(){
        previewView.restartPreview();
        onTakePick(false);
    }


    public void onTakePick(final boolean takePhoto)
    {
        Log.d("myapp", "onTakePick " + takePhoto);
        //este metodo esconde o menu da camera ou mostra o botao para tirar foto, simplesmente tem animações porque era codigo que ja tinha feito para outra app
        if(!takePhoto){
//            temp_pic.setVisibility(View.GONE);
        }

//        if(takePhoto){
//            getActivity().getEmojiManager().setEmojiconEditText(textMsg);
//            getActivity().getEmojiManager().setEmojiButton(emoji_btn);
//        }else {
//
//        }


        layout_before_photo.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
        layout_after_photo.setVisibility(takePhoto ? View.VISIBLE : View.GONE);

        Log.d("MyCameraApp", "layout_after_photo5:" + layout_after_photo.isShown());
    }


}