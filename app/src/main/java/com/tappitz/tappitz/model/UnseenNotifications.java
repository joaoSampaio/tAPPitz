package com.tappitz.tappitz.model;


import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.util.ModelCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UnseenNotifications {

    private Map<Integer,Integer> receivedPhotos;
    private Map<Integer,Integer> receivedComment;

    public UnseenNotifications(){
        this.receivedComment = new HashMap<>();
        this.receivedPhotos = new HashMap<>();
    }



    public void addReceivedPhoto(int receivedId){
        this.receivedPhotos.put(receivedId, receivedId);
    }

    public void addCommentPhoto(int pictureId, int vote){
        this.receivedComment.put(pictureId, vote);
    }


    public void save(){
        new ModelCache<UnseenNotifications>().saveModel(AppController.getAppContext(), this, Global.OFFLINE_UNSEEN);
    }

    public static UnseenNotifications load(){
        UnseenNotifications unseenNotifications = new ModelCache<UnseenNotifications>().loadModel(AppController.getAppContext(),new TypeToken<UnseenNotifications>(){}.getType(), Global.OFFLINE_UNSEEN);
        if(unseenNotifications == null)
            unseenNotifications = new UnseenNotifications();
        return unseenNotifications;
    }

    //authorName=Miguel Sampaio, action=NEW_PICTURE_VOTE, pictureId=272, date=Sun Dec 06 17:10:33 UTC 2015, vote=1, comment=, authorId=34


    //authorName => João Sampaio,  action => NEW_PICTURE_RECEIVED,  pictureId => 13,  date => Sun Mar 13 21:56:26 UTC 2016,  comment => ,  authorId => 2;




//    fromUserId => 2;
//    04-07 16:11:58.600 20069-20099/com.tappitz.tappitz D/notification:  fromUserEmail => joaosampaio30@gmail.com;
//    04-07 16:11:58.600 20069-20099/com.tappitz.tappitz D/notification:  action => NEW_FOLLOWER;
//    04-07 16:11:58.600 20069-20099/com.tappitz.tappitz D/notification:  fromUserName => João Sampaio;
//    04-07 16:11:58.600 20069-20099/com.tappitz.tappitz D/notification:  collapse_key => do_not_collapse;



//    private List<>


    public Map<Integer, Integer> getReceivedPhotos() {
        return receivedPhotos;
    }

    public Map<Integer, Integer> getReceivedComment() {
        return receivedComment;
    }
}
