package com.tappitz.app;

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


    public final static int NOTIFICATION_ID = 66;
    public static final String NOTIFICATION_COUNT = "NOTIFICATION_COUNT";

    public final static int INBOX_OP = 0;
    public final static int INBOX = 1;
    public final static int HOME = 2;
    public final static int OUTBOX = 3;
    public final static int OUTBOX_OP = 4;


    public final static int MIDDLE_QRCODE = 0;
    public final static int MIDDLE_BLANK = 1;
    public final static int MIDDLE_CONTACTS = 2;

    //public final static int FRIENDS = 3;



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
    public static final String VOTE_DATE_RESOURCE = "VOTE_DATE_RESOURCE";
    public static final String IS_TEMPORARY_RESOURCE = "IS_TEMPORARY_RESOURCE";
    public static final String TEMP_FINAL_RESOURCE = "TEMP_FINAL_RESOURCE";

    public final static int RED = 2;
    public final static int YELLOW = 1;
    public final static int GREEN = 0;


    //public static final String FONT1 = "fonts/SnackerComic.ttf";
    //public static final String FONT1 = "fonts/Goffik-O.ttf";
    public static final String FONT1 = "fonts/justice.ttf";


    public static final String NEW_FOLLOWER = "NEW_FOLLOWER";
    public static final String RELATION_DELETED = "RELATION_DELETED";
    public static final String BLOCKED_FRIEND = "BLOCKED_FRIEND";
    public static final String REMOVED_FRIEND_REQUEST = "REMOVED_FRIEND_REQUEST";
    public static final String NEW_FRIEND = "NEW_FRIEND";
    public static final String NEW_PICTURE_RECEIVED = "NEW_PICTURE_RECEIVED";
    public static final String NEW_PICTURE_VOTE = "NEW_PICTURE_VOTE";

    public static final String OPERATION_TYPE_REJECT = "REJECT";
    public static final String OPERATION_TYPE_ACCEPT = "ADD_FOLLOWER_AS_FRIEND";
    public static final String OPERATION_TYPE_INVITE = "FOLLOW";
    public static final String OPERATION_TYPE_UNDO_INVITE = "DELETE";
    public static final String OPERATION_TYPE_BLOCK = "BLOCK";
    public static final String OPERATION_TYPE_DELETE = "DELETE";



    public static final String OFFLINE_OUTBOX = "OFFLINE_OUTBOX";
    public static final String OFFLINE_INBOX = "OFFLINE_INBOX";
    public static final String OFFLINE_VOTE = "OFFLINE_VOTE";
    public static final String OFFLINE_WORK = "OFFLINE_WORK";
    public static final String OFFLINE_UNSEEN = "OFFLINE_UNSEEN";
    public static final String OFFLINE_VERSION = "OFFLINE_VERSION";


    public final static String FRIENDS = "FRIENDS";
    public final static String MYFOLLOWERS = "MYFOLLOWERS";
    public final static String FOLLOWING = "FOLLOWING";

    public final static String SCREEN_WIDTH = "screen_width";
    public final static String SCREEN_HEIGHT = "screen_height";

    public static final boolean VERSION_V2 = true;


    public static final String OPTIONS_TYPE = "OPTIONS_TYPE";
    public static final int OPTIONS_TYPE_INBOX  = 0;
    public static final int OPTIONS_TYPE_OUTBOX  = 1;
    public static final String OPTIONS_TITLE = "OPTIONS_TITLE";

}
