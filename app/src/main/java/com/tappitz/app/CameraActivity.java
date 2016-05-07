package com.tappitz.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


/**
 * Activity displaying the camera and mustache preview.
 *
 * @author Sebastian Kaspari <sebastian@androidzeitgeist.com>
 */
public class CameraActivity extends Activity {
    public static final String TAG = "Mustache/CameraActivity";

    /**
     * On activity getting created.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
    }

    /**
     * On fragment notifying about a non-recoverable problem with the camera.
     */

}