package com.tappitz.tappitz.rest;

import android.graphics.Bitmap;
import android.widget.ImageView;

//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.ImageRequest;

import java.util.HashMap;
import java.util.Map;


public class ImageLoaderWithSession  {
//extends ImageLoader
    private String sessionId;
//
//    /**
//     * Constructs a new ImageLoader.
//     *
//     * @param queue      The RequestQueue to use for making image requests.
//     * @param imageCache The cache to use as an L1 cache.
//     */
//    public ImageLoaderWithSession(RequestQueue queue, ImageCache imageCache) {
//        super(queue, imageCache);
//    }
//
//
//    @Override
//    protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, final String cacheKey) {
//        //return super.makeImageRequest(requestUrl, maxWidth, maxHeight, scaleType, cacheKey);
//
//        return new ImageRequest(requestUrl, new Response.Listener<Bitmap>() {
//            @Override
//            public void onResponse(Bitmap response) {
//                onGetImageSuccess(cacheKey, response);
//            }
//        }, maxWidth, maxHeight,
//                Bitmap.Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                onGetImageError(cacheKey, error);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                if(sessionId != null)
//                    params.put("Session-Id", sessionId);
//                return params;
//            }
//        };
//    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
