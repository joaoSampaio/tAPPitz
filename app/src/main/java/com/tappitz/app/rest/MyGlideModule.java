package com.tappitz.app.rest;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
//import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
//import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MyGlideModule  implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        client.interceptors().add(interceptor);

        OkHttpClient okHttpClient = client.build();

//        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(60, TimeUnit.SECONDS);
//        client.setReadTimeout(60, TimeUnit.SECONDS);
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(okHttpClient);
        glide.register(GlideUrl.class, InputStream.class, factory);
    }
}