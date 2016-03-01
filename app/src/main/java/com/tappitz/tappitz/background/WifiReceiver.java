package com.tappitz.tappitz.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.tappitz.tappitz.app.AppController;

/**
 * Created by joaosampaio on 24-02-2016.
 */
public class WifiReceiver extends BroadcastReceiver{
    private int time;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("wifi", "action:"+action);
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                //do stuff
                Log.d("wifi", "comecar o serviço");
                //iniciar o serviço se houver pedidos
//                time = 20000;
//
//                Runnable r = new TimerThread(20000);
//                new Thread(r).start();

                Intent background = new Intent(AppController.getAppContext(), BackgroundService.class);
                background.putExtra("origin","receiver");
                AppController.getAppContext().startService(background);


            } else {
                // wifi connection was lost
            }
        }
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
