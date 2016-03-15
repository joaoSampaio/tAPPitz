package com.tappitz.tappitz.rest;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.model.CreatePhoto;
import com.tappitz.tappitz.rest.model.ErrorLogEntry;
import com.tappitz.tappitz.rest.model.GoogleId;
import com.tappitz.tappitz.rest.model.UserLogin;
import com.tappitz.tappitz.rest.model.UserRegister;
import com.tappitz.tappitz.rest.model.VoteInbox;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

//import retrofit.Callback;
//import retrofit.http.Body;
//import retrofit.http.GET;
//import retrofit.http.POST;
//import retrofit.http.Path;

/**
 * Created by sampaio on 26-03-2015.
 */
public interface ApiV2 {


    @POST("/users/login")
        //void login(@Body LoginParams params, Callback<Response> response);
    Call<JsonElement> login(@Body UserLogin user);

    @POST("/users")
    Call<JsonElement> register(@Body UserRegister user);


    @POST("/google")
    Call<JsonElement> sendGoogleId(@Body GoogleId googleId);



    //Pede a lista de outbox
    @GET("/outbox")
    Call<JsonElement> requestOutbox();


    /*********InBox************/
    //Pede a lista de inbox
    @GET("/inbox")
    Call<JsonElement> requestInbox();


    //Envia o voto de uma foto recebida
    @POST("/pictures_votes")
    Call<JsonElement> sendVotePicture(@Body VoteInbox vote);


    @GET("/pictures/delete/{picture_id} ")
    Call<JsonElement> deletePhoto(@Path("picture_id") int picture_id);


    /*********Contactos************/

    /*List friends*/
    @GET("/friends")
    Call<JsonElement> listMyFriends();

    /*******Contactos - Pedidos de contacto********/
    @GET("/followers")
    Call<JsonElement> listMyFollowers();

    @GET("/following")
    Call<JsonElement> listFollowing();



    /*Contactos - Procura*/
    @GET("/contacts/{user_id}")
    Call<JsonElement> searchContact(@Path("user_id") String user_id);

    /*Contactos - Convidar*/
    @POST("/contacts")
    Call<JsonElement> operationContact(@Body ContactSendId id);



    /*Contactos - Convidar*/
    @POST("/contacts")
    Call<JsonElement> inviteContact(@Body ContactSendId id);

    /*Contactos - Convidar*/
    @POST("/contacts")
    Call<JsonElement> undoInviteContact(@Body ContactSendId id);

    /*Contactos - bloquear*/
    @POST("/contacts")
    Call<JsonElement> blockContact(@Body ContactSendId id);

    /*Contactos - apagar*/
    @POST("/contacts")
    Call<JsonElement> deleteContact(@Body ContactSendId id);

    @GET("/users/me")
    Call<JsonElement> isLogin();



    /*Contacto - aceitar*/
    @POST("/contacts")
    Call<JsonElement> answerContactRequest(@Body ContactSendId answer);



    /*********Create photo************/
    @POST("/pictures")
    Call<JsonElement> sendphoto(@Body CreatePhoto photo);


    @GET("/pictures/votes/{id_photo}")
    Call<JsonElement> getOutboxComments(@Path("id_photo") int id_photo);

    /***********SEND ERROR LOG************/
    @POST("/logError")
    Call<JsonElement> sendErrorLog(@Body ErrorLogEntry entry);

}