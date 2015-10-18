package com.tappitz.tappitz.rest;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.model.Photo;
import com.tappitz.tappitz.model.RequestId;
import com.tappitz.tappitz.model.UserLogin;
import com.tappitz.tappitz.model.UserRegister;
import com.tappitz.tappitz.model.photo_tAPPitz;
import com.tappitz.tappitz.rest.model.AnswerContactRequest;
import com.tappitz.tappitz.rest.model.ContactSendId;

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


//    @Multipart
//    @POST("/users/login")
//    //void login(@Body LoginParams params, Callback<Response> response);
//    void login(@Part("email") String email, @Part("password") String password, Callback<JsonElement> response);


    @POST("/users/login")
        //void login(@Body LoginParams params, Callback<Response> response);
    void login(@Body UserLogin user, Callback<JsonElement> response);

    @POST("/users")
    void register(@Body UserRegister user, Callback<JsonElement> response);


    //Pede a lista de outbox
    @GET("/users/outbox")
    void requestOutbox(Callback<List<Photo>> response);

    //Pede a imagem associada ao id
    @POST("/users/outbox")
    void requestOutbox(@Body RequestId request, Callback<JsonElement> response);


    @POST("/picture/comments")
    void requestPictureVotes(@Body RequestId request, Callback<JsonElement> response);



    /*Contactos*/
//    @GET("/contacts")
    @GET("/listMyContacts.json")
    void listMyContacts( Callback<JsonElement> callback);

    /*Contactos - Procura*/
    //@POST("/contacts")
    //void searchContact(@Body ContactSendId search, Callback<JsonElement> callback);
    @GET("/searchContact.json")
    void searchContact(Callback<JsonElement> callback);


    /*Contactos - Convidar*/
    @POST("/contacts/invite")
    void inviteContact(@Body ContactSendId id, Callback<JsonElement> callback);

    /*Contactos - Convidar*/
    @POST("/contacts/invite/undo")
    void undoInviteContact(@Body ContactSendId id, Callback<JsonElement> callback);


    /*Contactos - bloquear*/
    @POST("/contacts/block")
    void blockContact(@Body ContactSendId id, Callback<JsonElement> callback);

    /*Contactos - apagar*/
    @POST("/contacts/delete")
    void deleteContact(@Body ContactSendId id, Callback<JsonElement> callback);



    /*Contactos - Pedidos de contacto*/
//    @GET("/contacts/requests")
    @GET("/contactRequests.json")
    void listContactRequests( Callback<JsonElement> callback);


    /*Contacto - aceitar*/
    @POST("/contacts/request")
    void answerContactRequest(@Body AnswerContactRequest answer, Callback<JsonElement> callback);



    @GET("/56wu6")
    void teste( Callback<JsonElement> response);

    @GET("/43uz2")
    void getTapp( Callback<List<photo_tAPPitz>> response);


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