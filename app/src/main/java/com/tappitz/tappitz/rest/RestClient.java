package com.tappitz.tappitz.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tappitz.tappitz.Global;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by sampaio on 26-03-2015.
 */
public class RestClient {

    private static String sessionId;

    private static Api api;

    private static OkHttpClient okHttpClient;

    public static String getSessionId(){
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
        RestClient.sessionId = sessionId;
    }

    public static Api getApi(){
        return api;
    }

    /**
     * Injects cookies to every request
     */
    private static final RequestInterceptor COOKIES_REQUEST_INTERCEPTOR = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            Log.d("myapp", "***************RequestInterceptor:" + sessionId);
            if (null != sessionId && sessionId.length() > 0) {
                request.addHeader("Session-Id", sessionId);
            }
            request.addHeader("Content-type", "application/json;charset=UTF-8");
            request.addHeader("Accept", "application/json");
        }
    };

    public static final OkHttpClient getOk(){
        if(okHttpClient == null) {
            OkHttpClient picassoClient = new OkHttpClient();
            picassoClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Session-Id", sessionId)
                            .build();
                    return chain.proceed(newRequest);
                }
            });
            picassoClient.setConnectTimeout(2, TimeUnit.MINUTES);
            picassoClient.setReadTimeout(2, TimeUnit.MINUTES);
            picassoClient.setWriteTimeout(2, TimeUnit.MINUTES);
            okHttpClient = picassoClient;
        }

        return okHttpClient;
    }



    public static final Api getService() {
        if(RestClient.getApi() == null){
            OkHttpClient client = new OkHttpClient(); //create OKHTTPClient
            client.setConnectTimeout(2, TimeUnit.MINUTES);
            client.setReadTimeout(2, TimeUnit.MINUTES);
            client.setWriteTimeout(2, TimeUnit.MINUTES);
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            client.setCookieHandler(cookieManager); //finally set the cookie handler on client
            OkClient serviceClient = new OkClient(client);

            Gson gson = new GsonBuilder()
                    //.registerTypeAdapterFactory(new ItemTypeAdapterFactory()) // This is the important line ;)
                    .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                    .create();

            RestClient.api = new RestAdapter.Builder()
                    .setEndpoint(Global.ENDPOINT)
                    .setRequestInterceptor(COOKIES_REQUEST_INTERCEPTOR)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new GsonConverter(gson))
                    .setClient(serviceClient)
                    .build()
                    .create(Api.class);

        }



        return RestClient.getApi();
    }


}
