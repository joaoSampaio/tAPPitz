package com.tappitz.app.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.tappitz.app.app.AppController;

/**
 * Created by joaosampaio on 24-02-2016.
 */
public class WifiReceiver extends BroadcastReceiver{
    private int time;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("wifi", "action:"+action);
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            Log.d("Network Available ", "YES");
//            Intent background = new Intent(AppController.getAppContext(), BackgroundService.class);
//            background.putExtra("origin","receiver");
//            AppController.getAppContext().startService(background);
        } else {
            Log.d("Network Available ", "NO");
        }
        Intent background = new Intent(AppController.getAppContext(), BackgroundService.class);
        background.putExtra("origin","receiver");
        AppController.getAppContext().startService(background);


//        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
//            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
//                //do stuff
//                Log.d("wifi", "comecar o serviço");
//                //iniciar o serviço se houver pedidos
////                time = 20000;
////
////                Runnable r = new TimerThread(20000);
////                new Thread(r).start();
//
//                Intent background = new Intent(AppController.getAppContext(), BackgroundService.class);
//                background.putExtra("origin","receiver");
//                AppController.getAppContext().startService(background);
//
//
//            } else {
//                // wifi connection was lost
//            }
//        }
    }

    private class TimerThread implements Runnable{

        private int time;

        public TimerThread(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            for(int i = time; i > 0; i=i-1000) {
                Log.d("wifi", "seconds remaining: " + i / 1000);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("wifi", "seconds remaining FINISH ");
            AppController.getAppContext().startService(new Intent(AppController.getAppContext(), BackgroundService.class));
            return;
        }
    }

}
