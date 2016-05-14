package com.tappitz.app.camera;

import android.hardware.Camera;

/**
 * Created by Sampaio on 14/05/2016.
 */
public interface CameraInterface {

    void takePhoto(Camera.PictureCallback mPicture);
    void turnCamera();
    void toggleFlash();

}
