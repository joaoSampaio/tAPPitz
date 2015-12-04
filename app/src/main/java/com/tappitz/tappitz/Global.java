package com.tappitz.tappitz;

/**
 * Created by X220 on 19/08/2015.
 */
public class Global {

    public static final String KEY = "CHOICE_RESOURCE";

    //https://api.myjson.com/bins/56wu6
//    176.111.104.39
    public static final String ENDPOINT = "http://176.111.104.39:8182";
    public static final String ENDPOINT_SIGMA = "http://web.ist.utl.pt/ist170638/tappitz";
    //public static final String ENDPOINT = "https://api.myjson.com/bins";
//    public static final String ENDPOINT = "http://192.168.43.104:8182";

    // Google project id
    public static final String PROJECT_ID = "324070600220";




    public final static int HOME = 1;
    public final static int INBOX = 0;
    public final static int OUTBOX = 2;
    public final static int FRIENDS = 3;

    public static final int 	BROWSE_REQUEST = 2010;

    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_PASS = "KEY_PASS";

    public static final String IMAGE_RESOURCE_URL = "IMAGE_RESOURCE_URL";
    public static final String TEXT_RESOURCE = "TEXT_RESOURCE";
    public static final String ID_RESOURCE = "ID_RESOURCE";
    public static final String OWNER_RESOURCE = "OWNER_RESOURCE";
    public static final String DATE_RESOURCE = "DATE_RESOURCE";
    public static final String MYCOMMENT_RESOURCE = "MYCOMMENT_RESOURCE";
    public static final String HAS_VOTED_RESOURCE = "HAS_VOTED_RESOURCE";
    public static final String CHOICE_RESOURCE = "CHOICE_RESOURCE";

    public final static int RED = 2;
    public final static int YELLOW = 1;
    public final static int GREEN = 0;


    //public static final String FONT1 = "fonts/SnackerComic.ttf";
    //public static final String FONT1 = "fonts/Goffik-O.ttf";
    public static final String FONT1 = "fonts/justice.ttf";


    public static final String NEW_FRIEND_REQUEST = "NEW_FRIEND_REQUEST";
    public static final String UNDO_FRIEND_REQUEST = "UNDO_FRIEND_REQUEST";
    public static final String BLOCKED_FRIEND = "BLOCKED_FRIEND";
    public static final String REMOVED_FRIEND_REQUEST = "REMOVED_FRIEND_REQUEST";
    public static final String ACCEPTED_FRIEND_REQUEST = "ACCEPTED_FRIEND_REQUEST";
    public static final String NEW_PICTURE_RECEIVED = "NEW_PICTURE_RECEIVED";
    public static final String NEW_PICTURE_VOTE = "NEW_PICTURE_VOTE";


    public static final String OPERATION_TYPE_INVITE = "INVITE";
    public static final String OPERATION_TYPE_UNDO_INVITE = "UNDO_INVITE";
    public static final String OPERATION_TYPE_BLOCK = "BLOCK";
    public static final String OPERATION_TYPE_DELETE = "DELETE";


    public static final String OFFLINE_OUTBOX = "OFFLINE_OUTBOX";
    public static final String OFFLINE_INBOX = "OFFLINE_INBOX";
    public static final String OFFLINE_VOTE = "OFFLINE_VOTE";

}
