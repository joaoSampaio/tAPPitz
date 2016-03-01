package com.tappitz.tappitz.ui.toBeRemoved;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import com.tappitz.tappitz.R;

import java.io.IOException;

public class TestActivity extends Activity implements TextureView.SurfaceTextureListener {
private Camera mCamera;
private TextureView mTextureView;

protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        mTextureView = (TextureView)findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);
//        setContentView(mTextureView);
        }

public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    Log.d("Cam", "onSurfaceTextureAvailable");

    mCamera = Camera.open();

        try {
        mCamera.setPreviewTexture(surface);
        mCamera.startPreview();
        } catch (IOException ioe) {
        // Something bad happened
        }
        }

public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    Log.d("Cam", "onSurfaceTextureSizeChanged");

}

public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    Log.d("Cam", "onSurfaceTextureDestroyed");

    mCamera.stopPreview();
        mCamera.release();
        return true;
        }

public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame

        }
}

