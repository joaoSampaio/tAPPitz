package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
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

import com.bumptech.glide.Glide;
//import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.ScreenSlidePagerAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.background.BackgroundService;
import com.tappitz.tappitz.camera.CameraHelper;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.notification.RegistrationIntentService;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.RestClientV2;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CheckLoggedStateService;
import com.tappitz.tappitz.rest.service.LoginService;
import com.tappitz.tappitz.util.MainViewPager;
import com.tappitz.tappitz.util.NotificationCount;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class ScreenSlidePagerActivity extends FragmentActivity implements TextureView.SurfaceTextureListener{


    private boolean cameraReady;
    private boolean signIn;
//    View frame;
    View camera_buttons;
    private String sessionId;
    private HomeToBlankListener listenerCamera;
    private CameraBackPressed cameraBackPressed;
    private OutBoxFragment.UpdateAfterPicture updateAfterPicture;
    private MiddleContainerFragment.MiddleShowPage middleShowPage;
    private InBoxFragment.ReloadInbox reloadInboxListener;
    private BlankFragment.ButtonEnable buttonEnable;
    private int afterLoginAction = -1;

    //indica qual a picture a ser mostrada no inbox
    private int inbox_vote_id = -1;
    private PhotoInbox newPhoto;
    private Comment commentVote;
    private int outbox_id = -1;



    private Bundle extras;

    private Camera mCamera;
    private TextureView mTextureView;
    private Handler handler;
    private Runnable runLogin;

    private CameraHelper mHelper;
    private FrameLayout camera_preview;

    BackgroundService mService;
    boolean mBound = false;


    private View.OnClickListener goTolistener;

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
        Log.d("myapp_new", "****onCreate ");


        goTolistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("myapp_new", "****goTolistener ");
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
                        }
                        break;
                    case R.id.action_goto_contacts:
                        if(getMiddleShowPage() != null){
                            getMiddleShowPage().showPage(Global.MIDDLE_CONTACTS);
                        }
                        break;
                }
            }
        };


//        frame = findViewById(R.id.frame);
        extras = getIntent().getExtras();
        camera_buttons = findViewById(R.id.camera_buttons);
        camera_preview = (FrameLayout)findViewById(R.id.camera_preview);
//        mTextureView = (TextureView)findViewById(R.id.textureView);
//        mTextureView.setSurfaceTextureListener(this);

        // Instantiate a ViewPager and a PagerAdapter.

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("myapp_new", "****onResume onResume onResume: ");
        findViewById(R.id.splashScreen).bringToFront();
        findViewById(R.id.splashScreen).setVisibility(View.VISIBLE);
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

//        checkIsSignedIn();
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Isto é chamado quando a app está aberta e chega uma notificação, o utilizador não clicou ainda na notificação
            Log.d("myapp_new", "onReceive mMessageReceiver");
            //do other stuff here

            String action = "", pictureId;

            if(intent.hasExtra("action"))
                action = intent.getExtras().getString("action", "");
            if(action != null){
                Log.d("myapp", "****action: " + action);
//                switch (action){
//
//                }
                switch (action){
                    case Global.NEW_PICTURE_RECEIVED:
                        Log.d("myapp", "****NEW_PICTURE_RECEIVED: antes ");
                        if(getReloadInboxListener() != null) {
                            getReloadInboxListener().updateAfterVote();
                            Log.d("myapp", "****NEW_PICTURE_RECEIVED: depois ");
                        }

                        break;
                    case Global.NEW_PICTURE_VOTE:
                        Log.d("myapp", "****NEW_PICTURE_VOTE: antes ");

                        if(getUpdateAfterPicture() != null) {
                            getUpdateAfterPicture().refreshOfflineOutbox();
                            Log.d("myapp", "****NEW_PICTURE_VOTE: depois ");
                        }
                        break;
                }


            }
        }
    };

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("checkIsSignedIn", "**onPause**** ");
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
    }

    //determina o que acontece quando clica na notificação
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        extras = intent.getExtras();
        checkIsSignedIn();
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
        String action = "",pictureId;
        if(extras != null)
            action = extras.getString("action","");

        switch (action){
            case Global.NEW_PICTURE_RECEIVED:
                pictureId = extras.getString("pictureId", "-1");
                String pictureSentence = extras.getString("pictureSentence", "");
                String authorName = extras.getString("authorName", "");
                inbox_vote_id = Integer.parseInt(pictureId);
                newPhoto = new PhotoInbox(inbox_vote_id, pictureSentence, authorName);

                break;
            case Global.NEW_PICTURE_VOTE:

                pictureId = extras.getString("pictureId", "-1");
                String voteAuthorName = extras.getString("authorName", "");
                String comment = extras.getString("comment", "");
                String vote = extras.getString("vote", "-1");
                String votedDate = extras.getString("date", "");
                outbox_id = Integer.parseInt(pictureId);
                int voteInt = Integer.parseInt(vote);

                commentVote = new Comment(voteInt, voteAuthorName, votedDate);
                break;
        }

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


    public OutBoxFragment.UpdateAfterPicture getUpdateAfterPicture() {
        return updateAfterPicture;
    }

    public void setUpdateAfterPicture(OutBoxFragment.UpdateAfterPicture updateAfterPicture) {
        this.updateAfterPicture = updateAfterPicture;
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
        camera_buttons.setAlpha(alpha-1);
    }

    public void enableQRCodeCapture(boolean enable){
        Log.d("myapp2", "**--qr code enabled:"+enable);

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
}