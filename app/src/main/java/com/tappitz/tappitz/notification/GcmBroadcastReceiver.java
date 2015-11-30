package com.tappitz.tappitz.notification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        Log.d("updateMyActivity", "onReceive:" );

        updateMyActivity(context, intent.getExtras());
        setResultCode(Activity.RESULT_OK);
    }


    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void updateMyActivity(Context context, Bundle extras) {

        Log.d("updateMyActivity", "extras.size():" + extras.size());
        Log.d("updateMyActivity", "extras.toString():" + extras.toString());

        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d("updateMyActivity", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        Intent intent = new Intent("tAPPitz_1");
        Log.d("myapp3", "updateMyActivity");
        //put whatever data you want to send, if any
        intent.putExtras(extras);
        //send broadcast
        context.sendBroadcast(intent);
    }


}