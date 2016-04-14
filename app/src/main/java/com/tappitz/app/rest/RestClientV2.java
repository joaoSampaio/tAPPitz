package com.tappitz.app.rest;

import android.util.Log;

//import com.squareup.okhttp.Interceptor;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.tappitz.app.Global;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import retrofit.client.OkClient;
//import retrofit.converter.GsonConverter;
//import okhttp3.ConnectionPool;
//import okhttp3.Dispatcher;
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit.client.OkClient;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sampaio on 26-03-2015.
 */
public class RestClientV2 {

    private static String sessionId;

    private static ApiV2 api;

    private static OkHttpClient okHttpClient;

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
        RestClientV2.sessionId = sessionId;
    }

    public static ApiV2 getApi() {
        return api;
    }

    /**
     * Injects cookies to every request
     */
//    private static final Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
////            Response response = chain.proceed(chain.request());
//            Request request = chain.request();
//            Request.Builder newRequest;
//
//            newRequest = request.newBuilder()
//                    .addHeader("Content-type", "application/json;charset=UTF-8")
//                    .addHeader("Accept", "application/json");
//
//            if (null != sessionId && sessionId.length() > 0) {
//                newRequest.addHeader("Session-Id", sessionId);
//            }
//            Log.d("servico", "COOKIES_REQUEST_INTERCEPTOR");
//            return chain.proceed(newRequest.build());
//        }
//    };



    public static final OkHttpClient getOk() {
        if (okHttpClient == null) {

            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Content-type", "application/json;charset=UTF-8")
                            .addHeader("Accept", "application/json");

                    if (null != sessionId && sessionId.length() > 0) {
                        newRequest.addHeader("Session-Id", sessionId);
                    }
                    Log.d("servico", "COOKIES_REQUEST_INTERCEPTOR");
                    return chain.proceed(newRequest.build());
                }
            };

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .interceptors().add(COOKIES_REQUEST_INTERCEPTOR);
            client.interceptors().add(interceptor);

            okHttpClient = client.build();

        }

        return okHttpClient;
    }


    public static final ApiV2 getService() {
        if (RestClientV2.getApi() == null) {



            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Content-type", "application/json;charset=UTF-8")
                            .addHeader("Accept", "application/json");

                    if (null != sessionId && sessionId.length() > 0) {
                        newRequest.addHeader("Session-Id", sessionId);
                    }
                    Log.d("servico", "COOKIES_REQUEST_INTERCEPTOR");
                    return chain.proceed(newRequest.build());
                }
            };





            Dispatcher dispatcher=new Dispatcher();
            dispatcher.setMaxRequests(10);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .interceptors().add(COOKIES_REQUEST_INTERCEPTOR);
            client.interceptors().add(interceptor);
            client.dispatcher(dispatcher);

            client.connectionPool(new ConnectionPool(20, 5 * 60 ,TimeUnit.MINUTES));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Global.ENDPOINT)
                    .client(client.build())

                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RestClientV2.api = retrofit.create(ApiV2.class);
        }


        return RestClientV2.getApi();
    }


}
