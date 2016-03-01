package com.tappitz.tappitz.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.tappitz.tappitz.Global;

/**
 * Created by joaosampaio on 05-02-2016.
 */
public class NotificationCount {

    public static int addCount(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        int count = getCount(ctx);
        count++;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Global.NOTIFICATION_COUNT, count);
        editor.commit();
        return count;
    }

    public static int getCount(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        return sp.getInt(Global.NOTIFICATION_COUNT, 0);
    }

    public static void resetCount(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Global.NOTIFICATION_COUNT, 0);
        editor.commit();
    }


}
