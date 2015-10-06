package com.tappitz.tappitz.communication;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.model.photo_tAPPitz;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by sampaio on 26-03-2015.
 */
public interface Api {


    @Multipart
    @POST("/users/login")
    //void login(@Body LoginParams params, Callback<Response> response);
    void login(@Part("email") String email, @Part("password") String password, Callback<JsonElement> response);


    @GET("/56wu6")
    void teste( Callback<JsonElement> response);

    @GET("/outbox")
    void listMyOutbox( Callback<List<photo_tAPPitz>> callback);

    @GET("/outbox/{id_photo}")
    void getOutboxComments(@Path("id_photo") String id_photo, Callback<List<Comment>> response);

//    @Multipart
//    @POST("/login")
//    void loginPost(
//            @Part("username") String username,
//            @Part("password") String password,
//            @Part("remember_me") String remember_me,
//            @Part("csrf_token") String csrf_token,
//
//            Callback<Response> callback);
//
//
//
//    @Headers({
//            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp;q=0.8",
//    })
//    @GET("/login")
//    void loginGET(Callback<Response> response);
//
//    @Multipart
//    @POST("/index")
//    //void turnOnLight(@Part("Light_1") String Light_1, Callback<Response> response);
//    void turnOnLight(@PartMap Map<String, String> options, Callback<Response> response);
//
//
//
//
//    @GET("/index")
//    void indexGET(Callback<Response> response);
//
//    @GET("/logout")
//    void logout(Callback<Response> response);

}