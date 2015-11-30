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
import com.tappitz.tappitz.rest.ImageLoaderWithSession;
import com.tappitz.tappitz.util.LruBitmapCache;

import java.io.IOException;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoaderWithSession mImageLoader;

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
        mInstance = this;
        AppController.context = getApplicationContext();
//        Parse.initialize(this, "klnK5rk22iyv7pVQsNxBKV8x7cjwzCYZLlasKtgJ", "VJKKqDmJeXeJqCO8nAmfkquzaGyRo8QVa0n3EVSc");
//        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return AppController.context;
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

//    public void g(){
//        GlideUrl url = new GlideUrl("http://....", new LazyHeaders.Builder()
//                .addHeader("Session-Id", sessionId)
//                .build());
//        Glide.with(this).
//    }

//    public Picasso getPicasso() {
//        if(picasso == null){
//            OkHttpClient picassoClient = new OkHttpClient();
//
//            picassoClient.networkInterceptors().add(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request newRequest = chain.request().newBuilder()
//                            .addHeader("Session-Id", sessionId)
//                            .build();
//                    return chain.proceed(newRequest);
//                }
//            });
//
////            Picasso.Builder(
////            picasso =  Picasso.Builder(getApplicationContext()).downloader(new OkHttpDownloader(picassoClient)).build();
//        }
//        return picasso;
//    }
}