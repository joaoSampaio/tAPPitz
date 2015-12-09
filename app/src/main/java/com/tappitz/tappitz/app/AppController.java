package com.tappitz.tappitz.app;

import android.app.Application;
import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.rest.ImageLoaderWithSession;
import com.tappitz.tappitz.util.LruBitmapCache;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.IOException;

@ReportsCrashes(
        mailTo = "joaosampaio30@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static Context context;

    public Camera mCamera;
    public SurfaceHolder surfaceHolder;
    public int currentCameraId;
    public int width;
    public int height;
    private String sessionId;
    public boolean turnLightOn;
    private Picasso picasso;

    private static AppController mInstance;



    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        mInstance = this;
        AppController.context = getApplicationContext();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return AppController.context;
    }




    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}