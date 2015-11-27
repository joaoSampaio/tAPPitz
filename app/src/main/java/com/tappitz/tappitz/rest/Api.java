package com.tappitz.tappitz.rest;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.model.AnswerContactRequest;
import com.tappitz.tappitz.rest.model.ContactSendId;
import com.tappitz.tappitz.rest.model.CreatePhoto;
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






    //Pede a lista de outbox
    @GET("/outbox")
//    @GET("/outbox.json")
    void requestOutbox(Callback<JsonElement> response);

//    //Pede a imagem associada ao id
//    @POST("/users/outbox")
//    void requestOutbox(@Body RequestId request, Callback<JsonElement> response);
//
//
//    @POST("/picture/comments")
//    void requestPictureVotes(@Body RequestId request, Callback<JsonElement> response);




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
//    @GET("/searchContact.json")
//    void searchContact(Callback<JsonElement> callback);


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
//    @GET("/contactRequests.json")
    void listContactRequests( Callback<JsonElement> callback);


    /*Contacto - aceitar*/
    @POST("/contacts")
    void answerContactRequest(@Body ContactSendId answer, Callback<JsonElement> callback);






/*********Create photo************/
    @POST("/pictures")
//    @POST("/save_json.php")
    void sendphoto(@Body CreatePhoto photo, Callback<JsonElement> callback);








    @GET("/56wu6")
    void teste( Callback<JsonElement> response);

    @GET("/43uz2")
    void getTapp( Callback<List<PhotoOutbox>> response);


    @GET("/outbox")
    void listMyOutbox( Callback<List<PhotoOutbox>> callback);

    @GET("/pictures/votes/{id_photo}")
    void getOutboxComments(@Path("id_photo") int id_photo, Callback<List<Comment>> response);

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