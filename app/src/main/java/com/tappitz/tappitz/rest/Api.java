package com.tappitz.tappitz.rest;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.model.AnswerContactRequest;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.model.CreatePhoto;
import com.tappitz.tappitz.rest.model.ErrorLogEntry;
import com.tappitz.tappitz.rest.model.GoogleId;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.model.UserLogin;
import com.tappitz.tappitz.rest.model.UserRegister;
import com.tappitz.tappitz.rest.model.VoteInbox;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by sampaio on 26-03-2015.
 */
public interface Api {


    @POST("/users/login")
        //void login(@Body LoginParams params, Callback<Response> response);
    void login(@Body UserLogin user, Callback<JsonElement> response);

    @POST("/users")
    void register(@Body UserRegister user, Callback<JsonElement> response);


    @POST("/google")
    void sendGoogleId(@Body GoogleId googleId, Callback<JsonElement> callback);



    //Pede a lista de outbox
    @GET("/outbox")
    void requestOutbox(Callback<JsonElement> response);


    /*********InBox************/
    //Pede a lista de inbox
    @GET("/inbox")
//    @GET("/inbox.json")
    void requestInbox(Callback<JsonElement> response);


    //Envia o voto de uma foto recebida
    @POST("/pictures_votes")
    void sendVotePicture(@Body VoteInbox vote, Callback<JsonElement> response);





    /*********Contactos************/

    /*List Contacts*/
    @GET("/contacts")
//    @GET("/listMyContacts.json")
    void listMyContacts( Callback<JsonElement> callback);

    /*Contactos - Procura*/
    @GET("/contacts/{user_id}")
    void searchContact(@Path("user_id") String user_id , Callback<JsonElement> callback);

    /*Contactos - Convidar*/
    @POST("/contacts")
    void inviteContact(@Body ContactSendId id, Callback<JsonElement> callback);

    /*Contactos - Convidar*/
    @POST("/contacts")
    void undoInviteContact(@Body ContactSendId id, Callback<JsonElement> callback);

    /*Contactos - bloquear*/
    @POST("/contacts")
    void blockContact(@Body ContactSendId id, Callback<JsonElement> callback);

    /*Contactos - apagar*/
    @POST("/contacts")
    void deleteContact(@Body ContactSendId id, Callback<JsonElement> callback);

    @GET("/users/login")
    void isLogin(Callback<JsonElement> callback);

    /*******Contactos - Pedidos de contacto********/
    @GET("/contact_requests")
    void listContactRequests( Callback<JsonElement> callback);

    /*Contacto - aceitar*/
    @POST("/contacts")
    void answerContactRequest(@Body ContactSendId answer, Callback<JsonElement> callback);



    /*********Create photo************/
    @POST("/pictures")
    void sendphoto(@Body CreatePhoto photo, Callback<JsonElement> callback);


    @GET("/pictures/votes/{id_photo}")
    void getOutboxComments(@Path("id_photo") int id_photo, Callback<JsonElement> response);

    /***********SEND ERROR LOG************/
    @POST("/logError")
    void sendErrorLog(@Body ErrorLogEntry entry, Callback<JsonElement> response);

}