package com.tappitz.tappitz.app;

import android.app.Application;
import android.hardware.Camera;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tappitz.tappitz.rest.ImageLoaderWithSession;
import com.tappitz.tappitz.util.LruBitmapCache;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoaderWithSession mImageLoader;

    public Camera mCamera;
    public SurfaceHolder surfaceHolder;
    public int currentCameraId;
    public int width;
    public int height;
    private String sessionId;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
//        Parse.initialize(this, "klnK5rk22iyv7pVQsNxBKV8x7cjwzCYZLlasKtgJ", "VJKKqDmJeXeJqCO8nAmfkquzaGyRo8QVa0n3EVSc");
//        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            //mRequestQueue = new RequestQueue()
        }

        return mRequestQueue;
    }

    public ImageLoaderWithSession getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoaderWithSession(this.mRequestQueue,
                    new LruBitmapCache(20));
            mImageLoader.setSessionId(sessionId);
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        if(mImageLoader != null)
            mImageLoader.setSessionId(sessionId);

    }
}