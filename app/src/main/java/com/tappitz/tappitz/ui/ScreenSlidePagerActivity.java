package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.notification.RegistrationIntentService;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CheckLoggedStateService;
import com.tappitz.tappitz.rest.service.LoginService;
import com.tappitz.tappitz.util.MainViewPager;


import java.io.InputStream;


public class ScreenSlidePagerActivity extends FragmentActivity {


    private boolean cameraReady;
    private boolean signIn;
    View frame;
    private String sessionId;
    private InBoxFragment.OnNewPhotoReceived reloadInboxListener;
    private HomeToBlankListener listenerCamera;
    private CameraBackPressed cameraBackPressed;
    private OutBoxFragment.UpdateAfterPicture updateAfterPicture;
    private int afterLoginAction = -1;

    //indica qual a picture a ser mostrada no inbox
    private int inbox_vote_id = -1;


    /**
     * The number of pages (wizard steps) to show.
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
        Log.d("myapp_new", "****onCreate ");

        frame = findViewById(R.id.frame);
        // Instantiate a ViewPager and a PagerAdapter.

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("myapp_new", "****onResume onResume onResume: ");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("tAPPitz_1"));
        signIn = false;
        cameraReady = false;
        afterLoginAction = -1;
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

            //Isto é chamado quando a app está aberta e chega uma notificação, o utilizador não clicou ainda na notificação
            Log.d("myapp_new", "onReceive mMessageReceiver");
            //do other stuff here

            String action = null;
            Log.d("myapp", "****onReceive " + intent.hasExtra("action"));
            if(intent.getExtras() != null){
                Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

            }

            for (String key :  intent.getExtras().keySet()) {
                Object value =  intent.getExtras().get(key);
                Log.d("BroadcastReceiver 22 :", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }



            if(intent.hasExtra("action"))
                action = intent.getExtras().getString("action");
            if(action != null){
                Log.d("myapp", "****action: " + action);
                switch (action){

                    case Global.NEW_FRIEND_REQUEST:
                        showFriends();
                        break;
                    case Global.NEW_PICTURE_RECEIVED:
                        if(reloadInboxListener != null)
                            reloadInboxListener.refreshViewPager();
                        showPage(Global.INBOX);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = null;
        Log.d("myapp_new", "****onNewIntent " + intent.hasExtra("action"));
        if(intent.getExtras() != null){
            Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

        }

        if(intent.hasExtra("action"))
            action = intent.getExtras().getString("action");
        if(action != null){
            Log.d("myapp", "****action: " + action);
            switch (action){

                case Global.NEW_FRIEND_REQUEST:
                    showFriends();
//                    afterLoginAction = Global.FRIENDS;
                    break;
                case Global.NEW_PICTURE_RECEIVED:
                    showPage(Global.INBOX);
//                    afterLoginAction = Global.INBOX;
                    break;
            }

        }
    }



    private void openPageIfNotification(){
        if(afterLoginAction != -1){
            switch (afterLoginAction){

                case Global.FRIENDS:
                    showFriends();
                    break;
                case Global.INBOX:
                    showPage(Global.INBOX);
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



//        onSuccessSignIn();
//        if(true)
//            return;

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

        Log.d("myapp_new", "****onSuccessSignIn ");
        signIn = true;
        AppController.getInstance().setSessionId(sessionId);
                Glide.get(this)
                .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RestClient.getOk()));

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

                case Global.NEW_FRIEND_REQUEST:
                    showFriends();
                    break;

                case Global.NEW_PICTURE_RECEIVED:
                    showPage(Global.INBOX);
                    String pictureId = intent.getExtras().getString("pictureId", "-1");
                    inbox_vote_id = Integer.parseInt(pictureId);
                    break;

            }

        }else{
            mPager.setCurrentItem(1);
        }



        if(isCameraReady())
            closeSplashScreen();

        Log.d("myapp", "startService(intentRegister); ");
        Intent intentRegister = new Intent(this, RegistrationIntentService.class);
        startService(intentRegister);

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
        if(enable)
            bringToFrontmPager();
        else
            frame.bringToFront();

        Log.d("myapp", "enableSwipe " + enable);
    }

    @Override
    public void onBackPressed() {
        try {
            //se não houver um listener, ou seja, o fragmento da camera não tiver registado, ou se não tiver o menu após ter tirado a foto aberta
            if(getCameraBackPressed() == null || getCameraBackPressed().onBackPressed()) {
                if (mPager.getCurrentItem() != 1)
                    mPager.setCurrentItem(1);
                else {
                    finish();
                }
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

                case Global.INBOX:
//                    fragment = new BlankFragment();
                    fragment = new InBoxFragment();
                    break;
                case Global.HOME:
//                    fragment = new HomeFragment();
                    fragment = new BlankFragment();
                    break;
                case Global.OUTBOX:
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
         void onCameraAvailable();
    }

    public interface CameraBackPressed {
         boolean onBackPressed();
    }

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

    public int getInbox_vote_id() {
        return inbox_vote_id;
    }

    public void setInbox_vote_id(int inbox_vote_id) {
        this.inbox_vote_id = inbox_vote_id;
    }

    public OutBoxFragment.UpdateAfterPicture getUpdateAfterPicture() {
        return updateAfterPicture;
    }

    public void setUpdateAfterPicture(OutBoxFragment.UpdateAfterPicture updateAfterPicture) {
        this.updateAfterPicture = updateAfterPicture;
    }

    public CameraBackPressed getCameraBackPressed() {
        return cameraBackPressed;
    }

    public void setCameraBackPressed(CameraBackPressed cameraBackPressed) {
        this.cameraBackPressed = cameraBackPressed;
    }
}