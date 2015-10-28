package com.tappitz.tappitz.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.tappitz.tappitz.Global;

import java.net.CookieManager;
import java.net.CookiePolicy;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by sampaio on 26-03-2015.
 */
public class RestClient_Sigma {



    private static Api api;

    public static Api getApi(){
        return api;
    }

    /**
     * Injects cookies to every request
     */
    private static final RequestInterceptor COOKIES_REQUEST_INTERCEPTOR = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
//            if (null != cookies && cookies.length() > 0) {
//                //request.addHeader("Cookie", cookies);
//            }
            request.addHeader("Content-type", "application/json");
            request.addHeader("Accept", "application/json");
        }
    };

    public static final Api getService() {
        if(RestClient_Sigma.getApi() == null){
            OkHttpClient client = new OkHttpClient(); //create OKHTTPClient
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            client.setCookieHandler(cookieManager); //finally set the cookie handler on client
            OkClient serviceClient = new OkClient(client);

            Gson gson = new GsonBuilder()
                    //.registerTypeAdapterFactory(new ItemTypeAdapterFactory()) // This is the important line ;)
                    .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                    .create();

            RestClient_Sigma.api = new RestAdapter.Builder()
                    .setEndpoint(Global.ENDPOINT_SIGMA)
                    .setRequestInterceptor(COOKIES_REQUEST_INTERCEPTOR)
                    .setLogLevel(RestAdapter.LogLevel.HEADERS)
                    .setConverter(new GsonConverter(gson))
                    .setClient(serviceClient)
                    .build()
                    .create(Api.class);
        }
        return RestClient_Sigma.getApi();
    }


}
