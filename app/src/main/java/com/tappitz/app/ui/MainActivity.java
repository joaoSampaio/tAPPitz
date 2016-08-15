package com.tappitz.app.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.adapter.ScreenSlidePagerAdapter;
import com.tappitz.app.app.AppController;
import com.tappitz.app.background.BackgroundService;
import com.tappitz.app.camera.CallbackCameraAction;
import com.tappitz.app.camera.CameraHelper2;
import com.tappitz.app.camera.CameraPreview4;
import com.tappitz.app.model.ActivityResult;
import com.tappitz.app.model.FutureWorkList;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.model.SentPicture;
import com.tappitz.app.model.UnseenNotifications;
import com.tappitz.app.notification.RegistrationIntentService;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.service.CallbackMultiple;
import com.tappitz.app.rest.service.CheckLoggedStateService;
import com.tappitz.app.ui.secondary.ContactContainerFragment;
import com.tappitz.app.util.EmojiManager;
import com.tappitz.app.util.ListenerPagerStateChange;
import com.tappitz.app.util.MainViewPager;
import com.tappitz.app.util.ModelCache;
import com.tappitz.app.util.NotificationCount;
import com.tappitz.app.util.RefreshUnseenNotifications;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;


public class MainActivity extends FragmentActivity{


    private boolean cameraReady;
    private boolean signIn;
//    View frame;
    View camera_buttons;
    private HomeToBlankListener listenerCamera;
    private CameraBackPressed cameraBackPressed;
    private OutBoxFragment.ReloadOutbox reloadOutbox;
    private MiddleContainerFragment.MiddleShowPage middleShowPage;
    private ContactContainerFragment.ReloadAllContactsFragments reloadAllContactsFragments;
    private InBoxFragment.ReloadInbox reloadInboxListener;
    private BlankFragment.ButtonEnable buttonEnable;

    public List<Integer> screenHistory = new ArrayList<>();

    private Bundle extras;

    private Handler handler;
    private Runnable runLogin;

    //private CameraHelper mHelper;
    private CameraHelper2 mHelper;
    private ActivityResult imageGalleryResult;
    CameraPreview4 previewView;
    FrameLayout frame;

    BackgroundService mService;
    boolean mBound = false;
    private List<ListenerPagerStateChange> stateChange;
    boolean isRunning;

    private View.OnClickListener goTolistener;

    private TextView outbox_circle, inbox_circle;
    private EmojiManager emojiManager;

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
        frame = (FrameLayout)findViewById(R.id.camera);
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
                            getmHelper().enableQRCodeCapture(true);
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


    @Override
    public void onResume(){
        super.onResume();
        findViewById(R.id.splashScreen).bringToFront();
        findViewById(R.id.splashScreen).setVisibility(View.VISIBLE);
        handler = new Handler();
        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String email = sp.getString(Global.KEY_USER, "");
        AppController.getInstance().email = email;

        clearNotifications();


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("tAPPitz_1"));
        signIn = false;
        cameraReady = false;

        String sessionid = sp.getString("sessionId", "");
        if (Global.VERSION_V2) {
            RestClientV2.setSessionId(sessionid);
        } else {
            RestClient.setSessionId(sessionid);
        }
        emojiManager = new EmojiManager(this);
        try {

            previewView = new CameraPreview4(this, new CallbackCameraAction() {
                @Override
                public void onSuccess() {
                    closeSplashScreen();
                    if(getListenerCamera() != null)
                        getListenerCamera().onCameraAvailable();
                }

                @Override
                public void onFailure() {

                }
            });
            frame.addView(previewView);
            mHelper = new CameraHelper2(this, previewView);

        } catch (Exception exception) {
            exception.printStackTrace();
            Log.e("TAG", "Can't open camera with id ", exception);
            return;
        }


        //closeSplashScreen();
//        runLogin = new Runnable() {
//            @Override
//            public void run() {
//                checkIsSignedIn();
//            }
//        };
//        handler.postDelayed(runLogin, 200);
        checkIsSignedIn();
        refreshUnseenNotification();
        if(mHelper != null && imageGalleryResult != null){
            closeSplashScreen();
            mHelper.onActivityResult(imageGalleryResult.getRequestCode(), imageGalleryResult.getResultCode(), imageGalleryResult.getData());
            imageGalleryResult = null;
        }

    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(runLogin);

        frame.removeAllViews();

        if(mHelper != null) {
            mHelper.showBtnOptions(false);
            mHelper.onTakePick(false);

            if(getButtonEnable() != null){
                getButtonEnable().enableCameraButtons(true);
            }

            mHelper = null;
        }

        enableSwipe(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }



    //determina o que acontece quando clica na notificação
    @Override
    protected void onNewIntent(Intent intent) {
        try {
            super.onNewIntent(intent);
            extras = intent.getExtras();
            if(isRunning) {
                String action = intent.getExtras().getString("action", "");

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
            }else {
                checkIsSignedIn();
            }
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Global.NOTIFICATION_ID);
            NotificationCount.resetCount(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Isto é chamado quando a app está aberta e chega uma notificação, o utilizador não clicou ainda na notificação
            NotificationCount.resetCount(getApplicationContext());
            String action = "", pictureId;

            if(intent.hasExtra("action"))
                action = intent.getExtras().getString("action", "");
            if(action != null){

                switch (action){
                    case Global.NEW_PICTURE_RECEIVED:
                        if(getReloadInboxListener() != null) {
                            getReloadInboxListener().updateAfterVote();
                        }

                        break;
                    case Global.NEW_PICTURE_VOTE:

                        if(getReloadOutbox() != null) {
                            getReloadOutbox().refreshOfflineOutbox();
                        }
                        break;
                    case Global.NEW_FOLLOWER:
                        if(reloadAllContactsFragments != null)
                            reloadAllContactsFragments.onReloadAllContactsFragments();
                        break;
                    case Global.RELATION_DELETED:
                        if(reloadAllContactsFragments != null)
                            reloadAllContactsFragments.onReloadAllContactsFragments();
                        break;
                    case Global.NEW_FRIEND:
                        if(reloadAllContactsFragments != null)
                            reloadAllContactsFragments.onReloadAllContactsFragments();
                        break;
                    default:
//                        showPage(Global.HOME);
                }
                refreshUnseenNotification();


            }
        }
    };




    private void checkIsSignedIn(){

        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String sessionid = sp.getString("sessionId", "");
        Log.d("session", "sessionid:"+sessionid);
        if (Global.VERSION_V2) {
            RestClientV2.setSessionId(sessionid);
        } else {
            RestClient.setSessionId(sessionid);
        }
        if(BackgroundService.isWifiAvailable()) {

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
                    goToLoginActivity();
                }
            }).execute();
        }else{
            // nao há internet
            onSuccessSignIn();
        }

    }


    private void onSuccessSignIn(){
        //esconde splash screen e envia o id para notificações
        String action = "";
        if(extras != null)
            action = extras.getString("action","");

        signIn = true;
        //AppController.getInstance().setSessionId(sessionId);

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
            mService.registerClient(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void pass(View v){
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
                                mHelper.deletePhoto();
                        }
                    }else
                        finish();
                }
            }
        }catch (Exception e){
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

    public HomeToBlankListener getListenerCamera(){
        return listenerCamera;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Global.BROWSE_REQUEST && resultCode == Activity.RESULT_OK&& null != data) {

            imageGalleryResult = new ActivityResult(requestCode, resultCode, data);

        }
    }




    public void fadeCameraBts(float alpha){
//        Log.d("myapp2", "**--main activity alpha:"+(alpha-1));
        camera_buttons.setAlpha(alpha - 1);
    }

    public CameraHelper2 getmHelper() {
        return mHelper;
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

        try {
            UnseenNotifications unseenNotifications = UnseenNotifications.load();
            int commentsUnseen = unseenNotifications.getReceivedComment().size();
            int receivedUnseen = unseenNotifications.getReceivedPhotos().size();
            outbox_circle.setText("" + commentsUnseen);
            inbox_circle.setText("" + receivedUnseen);

            outbox_circle.setVisibility(commentsUnseen > 0 ? View.VISIBLE : View.GONE);
            inbox_circle.setVisibility(receivedUnseen > 0 ? View.VISIBLE : View.GONE);

            for (RefreshUnseenNotifications refresh : this.listenerUnseenNotifications) {
                refresh.onRefreshUnseenNotifications(unseenNotifications);
            }
        }catch (Exception e){
            Log.e("erro", " " + e.getMessage());
        }
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


    public void setReloadAllContactsFragments(ContactContainerFragment.ReloadAllContactsFragments reloadAllContactsFragments){
        this.reloadAllContactsFragments = reloadAllContactsFragments;
    }

    public EmojiManager getEmojiManager() {
        return emojiManager;
    }

    public void clearNotifications(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Global.NOTIFICATION_ID);
        NotificationCount.resetCount(getApplicationContext());
    }
}