package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.GoogleId;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CheckLoggedStateService;
import com.tappitz.tappitz.rest.service.LoginService;
import com.tappitz.tappitz.rest.service.SendGoogleIdService;
import com.tappitz.tappitz.util.MainViewPager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ScreenSlidePagerActivity extends FragmentActivity {


    private boolean cameraReady;
    private boolean signIn;
    View frame;
    private String sessionId;
    private InBoxFragment.OnNewPhotoReceived reloadInboxListener;
    private HomeToBlankListener listenerCamera;
//    private SplashScreenListener listenerSplash;


    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private MainViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT < 16) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
        setContentView(R.layout.activity_screen_slide);


        frame = findViewById(R.id.frame);
        // Instantiate a ViewPager and a PagerAdapter.

    }

    @Override
    public void onStart(){
        super.onStart();
        signIn = false;
        cameraReady = false;
        findViewById(R.id.splashScreen).setVisibility(View.VISIBLE);
        FragmentTransaction mCurTransaction = getSupportFragmentManager().beginTransaction();

        String tag = "home";
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mCurTransaction.attach(fragment);


        } else {
            fragment = new HomeFragment();
            mCurTransaction.add(R.id.frame, fragment, tag);
        }

        mCurTransaction.commitAllowingStateLoss();



//        onSuccessSignIn();
        checkIsSignedIn();
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("myapp3", "onReceive mMessageReceiver");
            //do other stuff here

            String action = null;
            Log.d("myapp", "****onReceive " + intent.hasExtra("action"));
            if(intent.getExtras() != null){
                Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

            }

            if(intent.hasExtra("action"))
                action = intent.getExtras().getString("action");
            if(action != null){
                Log.d("myapp", "****action: " + action);
                switch (action){

                    case Global.NOTIFICATION_ACTION_INVITE:
                        //showFriends();
                        break;
                    case Global.NOTIFICATION_ACTION_NEW_PHOTO:
                        if(reloadInboxListener != null)
                            reloadInboxListener.refreshViewPager();

                       // mPager.setCurrentItem(0);
                        break;
                }

            }




        }
    };

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }

    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("tAPPitz_1"));
//        if (Build.VERSION.SDK_INT >= 16) {
//            View decorView = getWindow().getDecorView();
//// Hide the status bar.
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = null;
        Log.d("myapp", "****onNewIntent " + intent.hasExtra("action"));
        if(intent.getExtras() != null){
            Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

        }

        if(intent.hasExtra("action"))
            action = intent.getExtras().getString("action");
        if(action != null){
            Log.d("myapp", "****action: " + action);
            switch (action){

                case Global.NOTIFICATION_ACTION_INVITE:
                    showFriends();
                    break;
                case Global.NOTIFICATION_ACTION_NEW_PHOTO:
                    mPager.setCurrentItem(0);
                    break;
            }

        }


    }


    private void checkIsSignedIn(){
//        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String sessionid = sp.getString("sessionId", "");
        RestClient.setSessionId(sessionid);

        final String email = sp.getString(Global.KEY_USER, "");
        final String password  = sp.getString(Global.KEY_PASS, "");
        Log.d("myapp", "**sessionid**** " + sessionid);
        Log.d("myapp", "**email**** " + sp.getString(Global.KEY_USER, ""));
        Log.d("myapp", "**password**** " + password);
        this.sessionId = sessionid;
        new CheckLoggedStateService(new CallbackMultiple() {
            @Override
            public void success(Object response) {
                //estou autenticado
                onSuccessSignIn();
            }

            @Override
            public void failed(Object error) {
                //houve erro ou não está autenticado, Mostrar Login Activity
                //Se tiver o email e password tento fazer sign in.
                Log.d("myapp", "**CheckLoggedStateService**** not signed in, email:" + email);
                if(email.length() > 0 && password.length() > 0){
                    new LoginService(email, password, new CallbackMultiple<String, String>() {
                        @Override
                        public void success(String session) {
                            Log.d("myapp", "**LoginService**** success:" + session);
                            if(session.length() > 0){
                                SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("sessionId", session);
                                editor.commit();
                                Log.d("myapp", "***login**sessionId*" + session);
                                RestClient.setSessionId(session);
                                sessionId = session;
                                onSuccessSignIn();
                            }
                        }

                        @Override
                        public void failed(String error) {
                            goToLoginActivity();
                        }
                    }).execute();

                }else {
                    goToLoginActivity();
                }
            }
        }).execute();
    }


    private void onSuccessSignIn(){
        //esconde splash screen e envia o id para notificações

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                findViewById(R.id.splashScreen).setVisibility(View.GONE);
//            }
//        }, 2000);

        signIn = true;
        AppController.getInstance().setSessionId(sessionId);
        mPager = (MainViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setClipChildren(false);
        mPager.setClipToPadding(false);

        Intent intent = getIntent();
        String action = null;
        Log.d("myapp", "****onCreate " + intent.hasExtra("action"));
        if(intent.getExtras() != null){
            Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

        }

        if(intent.hasExtra("action"))
            action = intent.getExtras().getString("action");
        if(action != null){
            Log.d("myapp", "****action: " + action);
            switch (action){

                case Global.NOTIFICATION_ACTION_INVITE:
                    showFriends();
                    break;
                case Global.NOTIFICATION_ACTION_NEW_PHOTO:
                    mPager.setCurrentItem(0);
                    break;
            }

        }else{
            mPager.setCurrentItem(1);
        }



        if(isCameraReady())
            closeSplashScreen();


        new SendGoogleIdService(getApplicationContext(), new CallbackMultiple() {
            @Override
            public void success(Object response) {

            }

            @Override
            public void failed(Object error) {

            }
        }).execute();
//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//
//                try {
//                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//
//                    String deviceToken = null;
//                    try {
//                        deviceToken = gcm.register(Global.PROJECT_ID);
//
//
//                        HttpClient httpclient = new DefaultHttpClient();
//                        HttpPost httppost = new HttpPost("http://web.ist.utl.pt/ist170638/tappitz/append_id.php");
//
//                        try {
//                            // Add your data
//                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//                            nameValuePairs.add(new BasicNameValuePair("id", deviceToken));
//                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                            // Execute HTTP Post Request
//                            HttpResponse response = httpclient.execute(httppost);
//
//                        } catch (ClientProtocolException e) {
//                            // TODO Auto-generated catch block
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("GCM", "Device token : " + deviceToken);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//        }.execute();
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void pass(View v){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("home");
        if (fragment != null && fragment.isVisible() && fragment instanceof HomeFragment) {
            ((HomeFragment)fragment).onClick(v);
        }
    }


    public void showPage(int page){
        if(mPagerAdapter != null && page >= 0 && page < NUM_PAGES){
            mPager.setCurrentItem(page);
        }
    }

    public void showFriends(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("friends");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        FriendsFragment newFragment = new FriendsFragment();
        newFragment.show(ft, "friends");
    }


    public void enableSwipe(boolean enable){
//        if(mPager != null)
//            mPager.setPagingEnabled(enable);
        if(enable)
            bringToFrontmPager();
        else
            frame.bringToFront();

        Log.d("myapp", "enableSwipe " + enable);
    }

    @Override
    public void onBackPressed() {
        try {
            if (mPager.getCurrentItem() != 1)
                mPager.setCurrentItem(1);
            else {
                finish();
            }
        }catch (Exception e){
            Log.d("myapp", "onback error");
        }
    }

    /**
     * A simple pager adapter that represents 5 OutBoxPageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){

                case 0:
//                    fragment = new BlankFragment();
                    fragment = new InBoxFragment();
                    break;
                case 1:
//                    fragment = new HomeFragment();
                    fragment = new BlankFragment();
                    break;
                case 2:
//                    fragment = new BlankFragment();
                    fragment = new OutBoxFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }


    public void bringToFrontmPager(){
        if(mPager != null)
            mPager.bringToFront();
    }

    public interface HomeToBlankListener {
        public void onCameraAvailable();
    }

    public interface SplashScreenListener {
        public void onCameraAvailable();
    }

//    public interface BlankToHomeListener {
//        public void getText(String text);
//    }

    public void callbackCameraAvailable(){
        try {
            if(listenerCamera != null)
                listenerCamera.onCameraAvailable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListenerCamera(HomeToBlankListener listenerCamera) {
        this.listenerCamera = listenerCamera;
    }


    public boolean isCameraReady() {
        return cameraReady;
    }

    public void setCameraReady(boolean cameraReady) {
        this.cameraReady = cameraReady;
    }

    public void notifyCameraReady(){
        setCameraReady(true);
        if(signIn)
            closeSplashScreen();


    }

    private void closeSplashScreen(){
        findViewById(R.id.splashScreen).setVisibility(View.GONE);
        signIn = false;
        cameraReady = false;
    }

    public void setReloadInboxListener(InBoxFragment.OnNewPhotoReceived reloadInboxListener) {
        this.reloadInboxListener = reloadInboxListener;
    }
}