package com.tappitz.tappitz.ui;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ScreenSlidePagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.background.BackgroundService;
import com.tappitz.tappitz.camera.CameraHelper;
import com.tappitz.tappitz.model.FutureWorkList;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.model.SentPicture;
import com.tappitz.tappitz.model.UnseenNotifications;
import com.tappitz.tappitz.notification.RegistrationIntentService;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CheckLoggedStateService;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.MainViewPager;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.NotificationCount;
import com.tappitz.tappitz.util.RefreshUnseenNotifications;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ScreenSlidePagerActivity extends FragmentActivity implements TextureView.SurfaceTextureListener{


    private boolean cameraReady;
    private boolean signIn;
//    View frame;
    View camera_buttons;
    private String sessionId;
    private HomeToBlankListener listenerCamera;
    private CameraBackPressed cameraBackPressed;
    private OutBoxFragment.ReloadOutbox reloadOutbox;
    private MiddleContainerFragment.MiddleShowPage middleShowPage;
    private InBoxFragment.ReloadInbox reloadInboxListener;
    private BlankFragment.ButtonEnable buttonEnable;
    private int afterLoginAction = -1;

    //indica qual a picture a ser mostrada no inbox
    private int inbox_vote_id = -1;
//    private PhotoInbox newPhoto;
//    private Comment commentVote;
//    private int outbox_id = -1;

    public List<Integer> screenHistory = new ArrayList<>();

    private Bundle extras;

    private Camera mCamera;
    private TextureView mTextureView;
    private Handler handler;
    private Runnable runLogin;

    private CameraHelper mHelper;
    private FrameLayout camera_preview;

    BackgroundService mService;
    boolean mBound = false;
    private List<ListenerPagerStateChange> stateChange;
    boolean isRunning;

    private View.OnClickListener goTolistener;

    private TextView outbox_circle, inbox_circle;

    private List<RefreshUnseenNotifications> listenerUnseenNotifications = new ArrayList<>();

    /**
     * The number of pages (wizard steps) to show.
     */
    private static final int NUM_PAGES = 5;

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
        setContentView(R.layout.activity_screen_slide);
        findViewById(R.id.splashScreen).bringToFront();
        findViewById(R.id.splashScreen).setVisibility(View.VISIBLE);
        outbox_circle = (TextView)findViewById(R.id.outbox_circle);
        inbox_circle = (TextView)findViewById(R.id.inbox_circle);
        outbox_circle.setVisibility(View.GONE);
        inbox_circle.setVisibility(View.GONE);
        requestPermissions();
        initOfflineValues();

        goTolistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.action_goto_sent:
                        showPage(Global.OUTBOX);
                        break;
                    case R.id.action_goto_received:
                        showPage(Global.INBOX);
                        break;
                    case R.id.action_goto_qrcode:
                        if(getMiddleShowPage() != null){
                            getMiddleShowPage().showPage(Global.MIDDLE_QRCODE);
                            enableQRCodeCapture(true);
                        }
                        break;
                    case R.id.action_goto_contacts:
                        if(getMiddleShowPage() != null){
                            getMiddleShowPage().showPage(Global.MIDDLE_CONTACTS);
                        }
                        break;
                    case R.id.inbox_circle:
                        if(getReloadInboxListener() != null){
                            UnseenNotifications unseenNotifications = UnseenNotifications.load();
                            if(!unseenNotifications.getReceivedPhotos().isEmpty()) {
                                int pictureId = unseenNotifications.getReceivedPhotos().entrySet().iterator().next().getValue();
                                getReloadInboxListener().openPageId(pictureId);
                                showPage(Global.INBOX);
                            }
                        }
                        break;
                    case R.id.outbox_circle:

                        showPage(Global.OUTBOX_OP);
                        break;
                }
            }
        };

        extras = getIntent().getExtras();
        camera_buttons = findViewById(R.id.camera_buttons);
        camera_preview = (FrameLayout)findViewById(R.id.camera_preview);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            mService.registerClient(null);
            unbindService(mConnection);
            mBound = false;
        }
        isRunning = false;
    }

    //determina o que acontece quando clica na notificação
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        extras = intent.getExtras();
        if(isRunning) {
            String action = intent.getExtras().getString("action", "");
            Log.d("myapp", "****onNewIntent action: " + action);

            switch (action){
                case Global.NEW_PICTURE_RECEIVED:
                    Log.d("myapp", "****onNewIntent NEW_PICTURE_RECEIVED: antes ");
                    showPage(Global.INBOX);
                    break;
                case Global.NEW_PICTURE_VOTE:
                    Log.d("myapp", "**** onNewIntent NEW_PICTURE_VOTE: antes ");
                    showPage(Global.OUTBOX);

                    break;
                default:
                    Log.d("myapp", "**** onNewIntent HOME: antes ");
                    showPage(Global.HOME);
            }
        }else {
            checkIsSignedIn();
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Global.NOTIFICATION_ID);
        NotificationCount.resetCount(getApplicationContext());
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.d("myapp_new", "****onResume onResume onResume: ");
        findViewById(R.id.splashScreen).bringToFront();
        findViewById(R.id.splashScreen).setVisibility(View.VISIBLE);

        if(!allPermissionsGiven()){
            return;
        }

        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String email = sp.getString(Global.KEY_USER, "");
        AppController.getInstance().email = email;

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Global.NOTIFICATION_ID);
        NotificationCount.resetCount(getApplicationContext());


        if(mHelper == null || !mHelper.requestedFile){
            start_camera();
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("tAPPitz_1"));
        signIn = false;
        cameraReady = false;
        afterLoginAction = -1;

        String sessionid = sp.getString("sessionId", "");
        if (Global.VERSION_V2) {
            RestClientV2.setSessionId(sessionid);
        } else {
            RestClient.setSessionId(sessionid);
        }
        //closeSplashScreen();
        runLogin = new Runnable() {
            @Override
            public void run() {
                checkIsSignedIn();
            }
        };
        handler = new Handler();
        handler.postDelayed(runLogin,200);


        if(mHelper == null)
            mHelper = new CameraHelper(this);
        mHelper.setUP();
        refreshUnseenNotification();
//        checkIsSignedIn();
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Isto é chamado quando a app está aberta e chega uma notificação, o utilizador não clicou ainda na notificação
            Log.d("myapp_new", "onReceive mMessageReceiver");
            //do other stuff here
            NotificationCount.resetCount(getApplicationContext());
            String action = "", pictureId;

            if(intent.hasExtra("action"))
                action = intent.getExtras().getString("action", "");
            if(action != null){
                Log.d("myapp", "****action: " + action);

                switch (action){
                    case Global.NEW_PICTURE_RECEIVED:
                        Log.d("myapp", "****NEW_PICTURE_RECEIVED: antes ");
                        if(getReloadInboxListener() != null) {
                            getReloadInboxListener().updateAfterVote();
                            Log.d("myapp", "****NEW_PICTURE_RECEIVED: depois ");
                            //showPage(Global.INBOX);
                        }

                        break;
                    case Global.NEW_PICTURE_VOTE:
                        Log.d("myapp", "****NEW_PICTURE_VOTE: antes ");

                        if(getReloadOutbox() != null) {
                            getReloadOutbox().refreshOfflineOutbox();
                            Log.d("myapp", "****NEW_PICTURE_VOTE: depois ");
                            //showPage(Global.OUTBOX);
                        }
                        break;
                    default:
//                        showPage(Global.HOME);
                }
                refreshUnseenNotification();


            }
        }
    };

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("checkIsSignedIn", "**onPause**** ");
        if(!allPermissionsGiven()){
            return;
        }
        handler.removeCallbacks(runLogin);
        if(mHelper != null) {
            mHelper.showBtnOptions(false);
            mHelper.onTakePick(false);

            if(getButtonEnable() != null){
                getButtonEnable().enableCameraButtons(true);
            }

            mHelper = null;
        }
        if(mCamera != null) {
            stop_camera();

        }
        destroyView();
        enableSwipe(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }



    private void checkIsSignedIn(){

        if(BackgroundService.isWifiAvailable()) {
            Log.d("checkIsSignedIn", "**checkIsSignedIn**** ");

            SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
            String sessionid = sp.getString("sessionId", "");
            if (Global.VERSION_V2) {
                RestClientV2.setSessionId(sessionid);
            } else {
                RestClient.setSessionId(sessionid);
            }

            final String email = sp.getString(Global.KEY_USER, "");
            final String password = sp.getString(Global.KEY_PASS, "");
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
                    goToLoginActivity();
                }
            }).execute();
        }else{
            // nao há internet
            Log.d("checkIsSignedIn", "**checkIsSignedIn nao há internet**** ");
            onSuccessSignIn();
        }

    }


    private void onSuccessSignIn(){
        Log.d("myapp", "**onSuccessSignIn()");
        //esconde splash screen e envia o id para notificações
        String action = "";
        if(extras != null)
            action = extras.getString("action","");



        signIn = true;
        AppController.getInstance().setSessionId(sessionId);

        if(Global.VERSION_V2)
                Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RestClientV2.getOk()));
        else
            Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RestClientV2.getOk()));

        mPager = (MainViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setClipChildren(false);
        mPager.setClipToPadding(false);

//        mPager.setPageTransformer(true, new CustomPageTransformer());
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                positionTab = position;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("myapp2", "**--seletcted Screen:" + position);
                if(position == Global.INBOX && getReloadInboxListener() != null){
                    getReloadInboxListener().InBoxSelected();
                }
                if(position == Global.OUTBOX && getReloadOutbox() != null){
                    getReloadOutbox().outBoxSelected();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (stateChange != null) {
                    for (ListenerPagerStateChange s : stateChange) {
                        s.onPageScrollStateChanged(state);
                    }
                }
            }
        });
        switch (action){
            case Global.NEW_PICTURE_RECEIVED:
                showPage(Global.INBOX);
                break;

            case Global.NEW_PICTURE_VOTE:
                showPage(Global.OUTBOX);
                break;
            default:
                showPage(Global.HOME);
        }

        //if( isCameraReady())
        closeSplashScreen();

        Intent intentRegister = new Intent(this, RegistrationIntentService.class);
        startService(intentRegister);
        isRunning = true;
    }

    int positionTab = 0;



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.registerClient(ScreenSlidePagerActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    public class CustomPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (positionTab < 3 && positionTab > 0) {
                View container = view.findViewById(R.id.container);

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left
                } else if (position <= 0) { // [-1,0]
                    // This page is moving out to the left

                    // Counteract the default swipe
                    //view.setTranslationX(pageWidth * -position);

                    if (container != null) {
                        // Fade the image in
                        container.setAlpha(1 + position);
                    }

                } else if (position <= 1) { // (0,1]
                    // This page is moving in from the right
                    if (container != null) {
                        // Fade the image out
                        container.setAlpha(1 - position);
                    }
                } else { // (1,+Infinity]
                    // This page is way off-screen to the right
                }
            }
        }
    }





    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void pass(View v){
        Log.d("HOME", "pass:" + v.getId());
        if(mHelper != null){
            mHelper.onClick(v);
        }
        if(goTolistener != null)
            goTolistener.onClick(v);

    }

    public void showPage(int page){
        if(mPagerAdapter != null && page >= 0 && page < NUM_PAGES){
            mPager.setCurrentItem(page);
        }
    }

    public void enableSwipe(boolean enable){
        if(enable)
            bringToFrontmPager();
        else
            findViewById(R.id.camera_buttons).bringToFront();

        Log.d("myapp", "enableSwipe " + enable);
    }

    @Override
    public void onBackPressed() {
        try {
            //se não houver um listener, ou seja, o fragmento da camera não tiver registado, ou se não tiver o menu após ter tirado a foto aberta
            if(getCameraBackPressed() == null || getCameraBackPressed().onBackPressed()) {
                if (mPager.getCurrentItem() != Global.HOME)
                    mPager.setCurrentItem(Global.HOME);
                else {
                    if(!screenHistory.isEmpty()){
                        if(screenHistory.get(0) == 0){
                            if(mHelper != null)
                                mHelper.deletePrevious();
                        }
                    }else
                        finish();
                }
            }
        }catch (Exception e){
            Log.d("myapp", "onback error");
            super.onBackPressed();
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
        Log.d("myapp", "****notifyCameraReady " );
        setCameraReady(true);
        if(signIn)
            closeSplashScreen();


    }

    private void closeSplashScreen(){
        Log.d("myapp", "****closeSplashScreen ");


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.splashScreen).setVisibility(View.GONE);
                signIn = false;
                cameraReady = false;
            }
        }, 1000);
    }



    public int getInbox_vote_id() {
        return inbox_vote_id;
    }


    public OutBoxFragment.ReloadOutbox getReloadOutbox() {
        return reloadOutbox;
    }

    public void setReloadOutbox(OutBoxFragment.ReloadOutbox reloadOutbox) {
        this.reloadOutbox = reloadOutbox;
    }

    public InBoxFragment.ReloadInbox getReloadInboxListener() {
        return reloadInboxListener;
    }

    public void setReloadInbox(InBoxFragment.ReloadInbox reloadInboxListener) {
        this.reloadInboxListener = reloadInboxListener;
    }

    public CameraBackPressed getCameraBackPressed() {
        return cameraBackPressed;
    }

    public void setCameraBackPressed(CameraBackPressed cameraBackPressed) {
        this.cameraBackPressed = cameraBackPressed;
    }

    public BlankFragment.ButtonEnable getButtonEnable() {
        return buttonEnable;
    }

    public void setButtonEnable(BlankFragment.ButtonEnable buttonEnable) {
        this.buttonEnable = buttonEnable;
    }


    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("Cam", "onSurfaceTextureAvailable");
        this.surface = surface;
        start_camera();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
        Log.d("Cam", "onSurfaceTextureSizeChanged");
//        updateTextureMatrix(orgPreviewWidth,orgPreviewHeight);

    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("Cam", "onSurfaceTextureDestroyed");
        stop_camera();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        System.out.println("IN onConfigurationChanged()");
        //setCameraDisplayOrientation(this, 1, mCamera);
    }

    SurfaceTexture surface;
    public void start_camera(){
        Log.d("cam", "start_camera " + (mCamera == null));
        if(mCamera != null)
            return;


        if( mTextureView == null){
             mTextureView = new TextureView(this);
            mTextureView.setSurfaceTextureListener(this);
            camera_preview.addView(mTextureView);
            return;
        }
        //surface = mTextureView.getSurfaceTexture();

        mCamera = Camera.open(AppController.getInstance().currentCameraId);

        try {
//            updateTextureMatrix(AppController.getInstance().width, AppController.getInstance().height);
            CameraHelper.setCameraDisplayOrientation(AppController.getInstance().currentCameraId, mCamera);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            AppController.getInstance().mCameraReady = true;

            Log.d("cam", "onCameraAvailable");
            callbackCameraAvailable();
            notifyCameraReady();
            if(mHelper != null){
                findViewById(R.id.btn_shutter).setVisibility(View.VISIBLE);
                mHelper.showBtnOptions(true);
                mHelper.onTakePick(false);
            }


        } catch (IOException ioe) {
            Log.d("cam", "ioe" + ioe.getMessage());
            // Something bad happened
        }
    }

    public void stop_camera(){
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            AppController.getInstance().mCameraReady = false;

        }
    }

    public void destroyView(){
        if( mTextureView != null){
            camera_preview.removeAllViews();
            mTextureView = null;
        }
    }

    public Camera getmCamera() {
        return mCamera;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("myapp", "onActivityResult main: " + requestCode);

        super.onActivityResult(requestCode, resultCode, data);
        if(mHelper == null) {
            mHelper = new CameraHelper(this);
        }
        if(requestCode == Global.BROWSE_REQUEST && resultCode == Activity.RESULT_OK&& null != data) {
            mHelper.requestedFile = true;
        }else {
            mHelper.requestedFile = false;
        }

        mHelper.onActivityResult(requestCode, resultCode, data);


    }

    private int orgPreviewWidth;
    private int orgPreviewHeight;
    private void updateTextureMatrix(int width, int height)
    {
        boolean isPortrait = false;

        Display display = getWindowManager().getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) isPortrait = true;
        else if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) isPortrait = false;

        int previewWidth = orgPreviewWidth;
        int previewHeight = orgPreviewHeight;

        if (isPortrait)
        {
            previewWidth = orgPreviewHeight;
            previewHeight = orgPreviewWidth;
        }

        float ratioSurface = (float) width / height;
        float ratioPreview = (float) previewWidth / previewHeight;

        float scaleX;
        float scaleY;

        if (ratioSurface > ratioPreview)
        {
            scaleX = (float) height / previewHeight;
            scaleY = 1;
        }
        else
        {
            scaleX = 1;
            scaleY = (float) width / previewWidth;
        }

        Matrix matrix = new Matrix();

        matrix.setScale(scaleX, scaleY);
        mTextureView.setTransform(matrix);

        float scaledWidth = width * scaleX;
        float scaledHeight = height * scaleY;

        float dx = (width - scaledWidth) / 2;
        float dy = (height - scaledHeight) / 2;
        mTextureView.setTranslationX(dx);
        mTextureView.setTranslationY(dy);
    }

    private static Pair<Integer, Integer> getMaxSize(List<Camera.Size> list)
    {
        int width = 0;
        int height = 0;

        for (Camera.Size size : list) {
            if (size.width * size.height > width * height)
            {
                width = size.width;
                height = size.height;
            }
        }

        return new Pair<Integer, Integer>(width, height);
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera)
    {
        if(camera != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);

            Camera.Parameters params = camera.getParameters();
            params.setRotation(result);
            camera.setParameters(params);
        }
    }

    public void fadeCameraBts(float alpha){
//        Log.d("myapp2", "**--main activity alpha:"+(alpha-1));
        camera_buttons.setAlpha(alpha - 1);
    }

    public void enableQRCodeCapture(boolean enable){
        Log.d("myapp2", "**--qr code enabled:" + enable);

        if(mHelper != null){
            mHelper.enableQRCodeScan(enable);
        }
    }

    public MiddleContainerFragment.MiddleShowPage getMiddleShowPage() {
        return middleShowPage;
    }

    public void setMiddleShowPage(MiddleContainerFragment.MiddleShowPage middleShowPage) {
        this.middleShowPage = middleShowPage;
    }

    public void addStateChange(ListenerPagerStateChange stateChange) {
        if(this.stateChange == null)
            this.stateChange = new ArrayList<ListenerPagerStateChange>();
        this.stateChange.add(stateChange);
    }
    public void removeStateChange(ListenerPagerStateChange stateChange) {
        if(this.stateChange != null) {
            this.stateChange.remove(stateChange);
        }
    }

    public MainViewPager getmPager() {
        return mPager;
    }


    public void refreshUnseenNotification(){

        UnseenNotifications unseenNotifications = UnseenNotifications.load();
        int commentsUnseen = unseenNotifications.getReceivedComment().size();
        int receivedUnseen = unseenNotifications.getReceivedPhotos().size();
        outbox_circle.setText(""+commentsUnseen);
        inbox_circle.setText(""+receivedUnseen);

        outbox_circle.setVisibility(commentsUnseen > 0 ? View.VISIBLE : View.GONE);
        inbox_circle.setVisibility(receivedUnseen>0? View.VISIBLE : View.GONE);

        for(RefreshUnseenNotifications refresh : this.listenerUnseenNotifications){
            refresh.onRefreshUnseenNotifications(unseenNotifications);
        }
    }

    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 120;
    private final static int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 121;
    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 122;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    private void requestPermissions(){



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public boolean allPermissionsGiven(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


        if(allPermissionsGiven()){
            onResume();
        }

//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CAMERA: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
    }


    private void initOfflineValues(){

        String unseenNotifications = new ModelCache<String>().loadModel(AppController.getAppContext(), new TypeToken<String>() {
        }.getType(), Global.OFFLINE_VERSION);
        if(unseenNotifications == null){


            new ModelCache<List<ReceivedPhoto>>().saveModel(AppController.getAppContext(), new ArrayList<ReceivedPhoto>(), Global.OFFLINE_INBOX);
            new ModelCache<FutureWorkList>().saveModel(AppController.getAppContext(), new FutureWorkList(), Global.OFFLINE_WORK);
            new ModelCache<List<SentPicture>>().saveModel(AppController.getAppContext(), new ArrayList<SentPicture>(), Global.OFFLINE_OUTBOX);
            new ModelCache<UnseenNotifications>().saveModel(AppController.getAppContext(), new UnseenNotifications(), Global.OFFLINE_UNSEEN);

            new ModelCache<String>().saveModel(AppController.getAppContext(), "existe", Global.OFFLINE_VERSION);
        }
    }

    public void addInterestUnseenNotification(RefreshUnseenNotifications refreshUnseenNotifications){
        this.listenerUnseenNotifications.add(refreshUnseenNotifications);
    }
    public void removeInterestUnseenNotification(RefreshUnseenNotifications refreshUnseenNotifications){
        this.listenerUnseenNotifications.remove(refreshUnseenNotifications);
    }


}