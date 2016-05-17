package com.tappitz.app.camera;

import android.hardware.Camera;

/**
 * Created by Sampaio on 14/05/2016.
 */
public interface CameraInterface {

    void takePhoto(Camera.PictureCallback mPicture, CallbackCameraAction callback);
    void turnCamera(CallbackCameraAction callback);
    void toggleFlash(CallbackCameraAction callback);

}
