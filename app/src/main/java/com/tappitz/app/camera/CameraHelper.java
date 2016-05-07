package com.tappitz.app.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.app.AppController;
import com.tappitz.app.background.BackgroundService;
import com.tappitz.app.model.FutureUpload;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.model.SentPicture;
import com.tappitz.app.ui.BlankFragment;
import com.tappitz.app.ui.ScreenSlidePagerActivity;
import com.tappitz.app.ui.secondary.QRCodeDialogFragment;
import com.tappitz.app.ui.secondary.SelectContactFragment;
import com.tappitz.app.util.AnimatedGifEncoder;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconEditText;

//import me.dm7.barcodescanner.zbar.BarcodeFormat;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class CameraHelper implements View.OnClickListener, View.OnLongClickListener {

    private final static int MAXFRAMES = 5;
    private ScreenSlidePagerActivity activity;
    private Button btn_shutter;
    private ImageButton btn_back;
    private String photoPath;
    private ImageView temp_pic;
    private boolean turnLightOn = false;
    public boolean requestedFile = false;
    RelativeLayout layout_after_photo, layout_before_photo;
    private EmojiconEditText textMsg;
    private ImageView emoji_btn;
    private byte[] photoData;
    private int previewCount = 0;
    private int qrCodeSampleTime = 5;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean isLongClickActive = false;
    ArrayList<Bitmap> bitmapsGif;
    private int numFrames = 0;
    private Handler handler;
    private Runnable gifRunnable;
    private Camera mCamera;

    private FrameLayout camera_preview;
    CameraPreview4 previewView;

    final static int[] CLICABLES = {R.id.camera_options, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept};

    static {
        System.loadLibrary("iconv");
    }


    public CameraHelper(ScreenSlidePagerActivity act) {
        this.activity = act;

        isLongClickActive = false;
        bitmapsGif = new ArrayList<>();
        handler = new Handler();
        gifRunnable = new Runnable() {
            @Override
            public void run() {
                if(isLongClickActive && numFrames < MAXFRAMES) {
                    Log.d("gif", "Runnable:"+numFrames);
                    addFrameWhenAvailable();
                    if (handler != null)
                        handler.postDelayed(this, 1 * 1000);
                }

            }
        };
        btn_back  = (ImageButton) activity.findViewById(R.id.btnPhotoDelete);

        btn_shutter = (Button) activity.findViewById(R.id.btn_shutter);
        photoPath = "";
        temp_pic = (ImageView) activity.findViewById(R.id.temp_pic);

        layout_after_photo = (RelativeLayout)activity.findViewById(R.id.layout_after_photo);
        layout_before_photo = (RelativeLayout)activity.findViewById(R.id.layout_before_photo);

        textMsg = (EmojiconEditText)activity.findViewById(R.id.textMsg);
        emoji_btn = (ImageView) activity.findViewById(R.id.emoji_btn);

        layout_after_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp2", "ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here

                        imm.hideSoftInputFromWindow(btn_shutter.getWindowToken(), 0);
                        layout_after_photo.setVisibility(View.INVISIBLE);

                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp2", "ACTION_UP");
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("myapp2", "ACTION_POINTER_UP");
                        layout_after_photo.setVisibility(View.VISIBLE);
                        layout_after_photo.bringToFront();
                        //=====Write down you code Finger Released code here
//                        if(textMsgWrapper.isShown()){
//                            imm.showSoftInput(textMsg, InputMethodManager.SHOW_IMPLICIT);
//                        }
                        return true;
                }

                return false;
            }
        });

        for(int id: CLICABLES)
            getActivity().findViewById(id).setOnClickListener(this);

        btn_shutter.setOnClickListener(this);
    }

    private void setUpSize(){
        SharedPreferences sp = getActivity().getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        int width = sp.getInt(Global.SCREEN_WIDTH, 0);
        int height = sp.getInt(Global.SCREEN_HEIGHT, 0);
        if( width == 0) {

            Display d = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            width = d.getWidth();
            height = d.getHeight();
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Global.SCREEN_WIDTH, width);
            editor.putInt(Global.SCREEN_HEIGHT, height);
            editor.commit();
        }
        AppController.getInstance().width = width;
        AppController.getInstance().height = height;
    }

    public void setUP(){
        Log.d("MyCameraApp", "setUP home");

        btn_shutter.setVisibility(View.GONE);

        camera_preview = (FrameLayout)getActivity().findViewById(R.id.camera_preview);
//        previewView = new CameraPreview4(getActivity());
//        camera_preview.addView(previewView);

        startCamera();
//        mCamera = Camera.open(AppController.getInstance().currentCameraId);
//        determineDisplayOrientation();
//        previewView.setCamera(mCamera);




        setUpSize();


        Log.d("MyCameraApp", "setUP home requestedFile:" + requestedFile);
        if(!requestedFile) {
            temp_pic.setVisibility(View.GONE);
            showBtnOptions(false);
            Log.d("MyCameraApp", "layout_after_photo1:" + layout_after_photo.isShown());
            layout_after_photo.setVisibility(View.GONE);
            Log.d("MyCameraApp", "layout_after_photo2:" + layout_after_photo.isShown());
            onTakePick(false);


        }else {
            temp_pic.setVisibility(View.VISIBLE);
            onTakePick(true);
        }



        showBtnOptions(true);
        Log.d("MyCameraApp", "layout_after_photo3:" + layout_after_photo.isShown());
    }


    public void destroyCamera(){
        stopCamera();
        camera_preview.removeAllViews();
    }

    public void startCamera(){
        Log.d("MyCameraApp", "start camera .....>>>>>:");
        camera_preview.removeAllViews();
        previewView = new CameraPreview4(getActivity());
        camera_preview.addView(previewView);


        mCamera = Camera.open(AppController.getInstance().currentCameraId);
        determineDisplayOrientation();
        previewView.setCamera(mCamera);
        AppController.getInstance().mCameraReady = true;

        if(getActivity().getListenerCamera() != null)
            getActivity().getListenerCamera().onCameraAvailable();
        getActivity().notifyCameraReady();
        btn_shutter.setVisibility(View.VISIBLE);
        showBtnOptions(true);
        onTakePick(false);
    }

    public void stopCamera(){

        if (mCamera != null) {
            previewView.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        //camera_preview.removeAllViews();
    }


    public void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(AppController.getInstance().currentCameraId, cameraInfo);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees  = 0;

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

        int displayOrientation;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(displayOrientation);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.set("jpeg-quality", 90);


//        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
//            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
//            case Surface.ROTATION_180: degrees = 180; break;//Upside down
//            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
//        }
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        int rotate = (info.orientation - degrees + 360) % 360;
        displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        parameters.setRotation(displayOrientation);

        mCamera.setParameters(parameters);
    }

    private void onCameraAvailable(){
        if(getActivity() != null) {
            Log.d("cam", "onCameraAvailable");
            ( getActivity()).callbackCameraAvailable();
            ( getActivity()).notifyCameraReady();
        }


        btn_shutter.setVisibility(View.VISIBLE);
        showBtnOptions(true);
    }

    public static void setCameraDisplayOrientation(
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        AppController app = AppController.getInstance();
        int degrees = 0;
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            //Choose another supported mode
        }

        //temos de trocar os valores caso a width seja mais baixa que o height
        int maxWidth = app.width > app.height? app.width : app.height;
        int maxHeight = app.width > app.height? app.height : app.width;



        Log.d("MyCameraApp", "AppController.getInstance().turnLightOn: " + AppController.getInstance().turnLightOn);
        if (AppController.getInstance().turnLightOn) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            //nao funciona
            List<Camera.Size> sizes = sizes = parameters.getSupportedPreviewSizes();
            Camera.Size sizeScreen = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {

                if (sizes.get(i).width > sizeScreen.width)
                    sizeScreen = sizes.get(i);
                if(sizes.get(i).height == maxHeight) {
                    sizeScreen = sizes.get(i);
                    break;
                }
            }
            Log.d("MyCameraApp", "sizeScreen size.width: " + sizeScreen.width + " size.height: " + sizeScreen.height);
            parameters.setPreviewSize(sizeScreen.width, sizeScreen.height);

            sizes = parameters.getSupportedPictureSizes();
            Camera.Size sizeCamera = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {
                Log.d("myapp", "size.width: " +sizes.get(i).width + " size.height: " + +sizes.get(i).height);
                if (sizes.get(i).width > sizeCamera.width)
                    sizeCamera = sizes.get(i);
                if(sizes.get(i).height == maxHeight) {
                    sizeCamera = sizes.get(i);
                    break;
                }
            }

            Log.d("MyCameraApp", "best size.width: " + sizeCamera.width + " size.height: " + sizeCamera.height);
            parameters.setPictureSize(sizeCamera.width, sizeCamera.height);
        }

        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.set("jpeg-quality", 90);

        parameters.set("orientation", "portrait");
        degrees = 0;
        degrees = 90;
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            degrees = 270;
        //parameters.set("rotation", degrees);
        parameters.setRotation(degrees);
        camera.setParameters(parameters);
    }

    public void loadBitmapFile(ImageView imageView,String path, int width, int height) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, path, width, height);
        task.setListener(new BitmapWorkerTask.SaveNewRotatedPictureInterface() {
            @Override
            public void onSaveToFileRotated(Uri uri, String photoPathNew) {
                Log.d("myapp", "onSaveToFileRotated ");
                photoPath = photoPathNew;
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                btn_back.setEnabled(true);
            }
        });
        task.execute();
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap

            photoData = data;
            stop_camera();

            new SavePhotoBackgroundTask(photoData, new SavePhotoBackgroundTask.SaveNewRotatedPictureInterface() {
                @Override
                public void onSaveToFileRotated(Uri uri, String photoNewPath) {
                    //se houve erro e o uri estiver a null mostramos erro ao utilizador
                    if(uri == null ){
                        if(activity != null){
                            Toast.makeText(activity, "There was a problem with the picture try again.", Toast.LENGTH_LONG);
                            btn_back.setEnabled(true);
                        }
                    }else{

                        photoPath = UriPath.getPath(activity, uri);
//                        loadBitmapFile(temp_pic, photoPath, AppController.getInstance().width, AppController.getInstance().height);
                        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        photoData = null;
                        Log.d("myapp", "1 setEnabled(true)");
                        Toast.makeText(activity, "done", Toast.LENGTH_LONG);
//                        btn_back.setEnabled(true);

                    }

                }
            }).execute();

        }
    };

    public void deletePrevious(){
        //recomeça a camera caso fosse foto da galeria

        textMsg.setText("");
        photoPath = "";
        photoData = null;
        start_camera();
        ( activity).enableSwipe(true);

        recycleImagesFromView(temp_pic);
        onTakePick(false);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btn_shutter.getWindowToken(), 0);
        if(!getActivity().screenHistory.isEmpty())
            getActivity().screenHistory.remove(0);
    }

    public static void recycleImagesFromView(ImageView view) {
        Log.d("myapp", "recycleImagesFromView");
        view.setVisibility(View.GONE);
        Drawable drawable = view.getDrawable();
        Log.d("myapp", "drawable != null" + (drawable != null));
        Log.d("myapp", "drawable instanceof BitmapDrawable" + (drawable instanceof BitmapDrawable));
        if(drawable != null & drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            if (bitmapDrawable != null &&  bitmapDrawable.getBitmap() != null && !bitmapDrawable.getBitmap().isRecycled()) {
                Log.d("myapp", "foi reciclado");
                Bitmap bitmap = bitmapDrawable.getBitmap();
                view.setImageBitmap(null);
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public void onTakePick(final boolean takePhoto)
    {
        Log.d("myapp", "onTakePick " + takePhoto);
        //este metodo esconde o menu da camera ou mostra o botao para tirar foto, simplesmente tem animações porque era codigo que ja tinha feito para outra app
        if(!takePhoto){
            temp_pic.setVisibility(View.GONE);
        }

        if(takePhoto){
            getActivity().getEmojiManager().setEmojiconEditText(textMsg);
            getActivity().getEmojiManager().setEmojiButton(emoji_btn);
        }else {

        }

        
        layout_before_photo.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
        layout_after_photo.setVisibility(takePhoto ? View.VISIBLE : View.GONE);

        Log.d("MyCameraApp", "layout_after_photo5:" + layout_after_photo.isShown());
    }

    public void showBtnOptions(boolean show){
        Log.d("myapp", "showBtnOptions:"+show);
        activity.findViewById(R.id.camera_options).setVisibility(!show ? View.GONE : View.VISIBLE);
        activity.findViewById(R.id.layout_camera).setVisibility(View.GONE);
    }



    @Override
    public void onClick(View v) {
        View view;
        switch (v.getId()){
            case R.id.camera_options:

                if(v.getTag() != null && v.getTag() instanceof String){
                    activity.findViewById(R.id.layout_camera).setVisibility(View.GONE);
                    return;
                }
                view = getActivity().findViewById(R.id.layout_camera);
                view.setVisibility(view.isShown()? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_shutter:

                Log.d("myapp", "btn_shutter");

                if(getActivity().getmCamera() != null) {
                    //btn_back.setEnabled(false);
                    getActivity().getmCamera().takePicture(null, null, mPicture);
                    (activity).enableSwipe(false);
                    onTakePick(true);
                    getActivity().screenHistory.add(0, 0);
                }
                break;

            case R.id.btnPhotoDelete:

                Log.d("myapp", "btnPhotoDelete");
                deletePrevious();

                break;

            case R.id.btnPhotoAccept:

                if(photoPath == null || photoPath.equals("")) {
                    final ProgressDialog progressDialog = ProgressDialog.show(activity, "Please wait", "We're saving the photo to file.", true);
                    new SavePhotoBackgroundTask(photoData, new SavePhotoBackgroundTask.SaveNewRotatedPictureInterface() {
                        @Override
                        public void onSaveToFileRotated(Uri uri, String photoNewPath) {

                            progressDialog.dismiss();

                            //se houve erro e o uri estiver a null mostramos erro ao utilizador
                            if(uri == null){
//                                Toast.makeText(, "There was a problem with the picture try again.", Toast.LENGTH_LONG);
                            }else{
                                photoPath = photoNewPath;
                                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                photoData = null;
                                showDialog();
                            }
                        }
                    }).execute();
                }else {
                    Log.d("myapp", "photoPath has uri");
                    showDialog();
                }

                break;

            case R.id.btn_load:

                stop_camera();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, "Seleccionar ficheiro"), Global.BROWSE_REQUEST);

                break;
            case R.id.btn_flash:
                Log.d("myapp", "btn_flash:");
                try {
                    enableCameraButtons(false);
                    if(getActivity().getmCamera() == null) {
                        enableCameraButtons(true);
                        return;
                    }
                    Camera.Parameters params = getActivity().getmCamera().getParameters();
                    Button b = (Button)activity.findViewById(R.id.btn_flash);

                    turnLightOn = !turnLightOn;
                    AppController.getInstance().turnLightOn = turnLightOn;

                    if (turnLightOn) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        getActivity().getmCamera().setParameters(params);
                        getActivity().getmCamera().startPreview();
                        b.setTextColor(Color.YELLOW);
                    } else {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        getActivity().getmCamera().setParameters(params);
                        getActivity().getmCamera().startPreview();
                        b.setTextColor(Color.WHITE);
                    }
                    enableCameraButtons(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    enableCameraButtons(true);
                }
                break;
            case R.id.btn_toggle_camera:
                Log.d("myapp", "btn_toggle_camera:");
                enableCameraButtons(false);
                PackageManager pm = getActivity().getPackageManager();

                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                    enableCameraButtons(true);
                    Toast.makeText(getActivity(), "You only have one camera!", Toast.LENGTH_SHORT).show();
                    break;
                }

                if(AppController.getInstance().currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }

                stop_camera(new ControlCameraTask.CallbackCamera() {
                    @Override
                    public void onDone() {
                        Log.d("myapp", "btn_toggle_camera onDone:");
                        start_camera();
                        enableCameraButtons(true);
                    }

                    @Override
                    public void onError() {
                        Log.d("myapp", "btn_toggle_camera onError:");
                        stop_camera();
                        start_camera();
                        enableCameraButtons(true);
                    }
                });
                break;

        }
    }


    private void enableCameraButtons(boolean enable){

        BlankFragment.ButtonEnable listener = ( activity).getButtonEnable();
        if(listener != null){
            listener.enableCameraButtons(enable);
        }
    }

    void showDialog() {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final String comment = textMsg.getText().toString();
        final SelectContactFragment newFragment = new SelectContactFragment();
        newFragment.setListener(new SelectContactFragment.OnSelectedContacts() {
            @Override
            public void sendPhoto(final List<Integer> contacts, boolean sendFollowers) {



                SentPicture out = new SentPicture(comment, photoPath);
                //actualiza e guarda offline
                if (getActivity().getReloadOutbox() != null) {
                    getActivity().getReloadOutbox().updateTemporaryOutbox(out);
                }
                //adiciona work futuro
                FutureUpload upload = out.generateFutureWork(contacts, sendFollowers);
                BackgroundService.addPhotoUploadWork(upload);



                //progressDialog.dismiss();
                deletePrevious();

                //lançar o serviço
                AppController.getAppContext().startService(new Intent(AppController.getAppContext(), BackgroundService.class));
                newFragment.dismiss();
            }
        });

        ft.add(newFragment, "dialog");
        ft.commitAllowingStateLoss();

        //newFragment.show(ft, "dialog");

    }

    public ScreenSlidePagerActivity getActivity() {
        return activity;
    }

    public void stop_camera(){
        getActivity().stop_camera();
    }

    private void stop_camera(ControlCameraTask.CallbackCamera call) {
        stop_camera();
        call.onDone();
    }

    private void start_camera() {
        getActivity().start_camera();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myapp", "onActivityResult: " + requestedFile);
        if(requestCode == Global.BROWSE_REQUEST && resultCode == Activity.RESULT_OK&& null != data) {
            Log.d("myapp", "onActivityResult dentro 1");
            stop_camera();
            (getActivity()).enableSwipe(false);
            Uri selectedImageUri = data.getData();
            photoPath = UriPath.getPath(getActivity(), selectedImageUri);
            requestedFile = true;
            loadBitmapFile(temp_pic, photoPath, AppController.getInstance().width, AppController.getInstance().height);
            onTakePick(true);
        }else {
            requestedFile = false;
            ( getActivity()).enableSwipe(true);
        }
    }


    public void enableQRCodeScan(boolean enable){
        Log.d("myapp", "*************************enableQRCodeScan: " + enable);
        if(enable) {
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            scanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
//            scanner.setConfig(BarcodeFormat.QRCODE.getId(), Config.ENABLE, 1);
            scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);

            barcodeScanned = false;
            if (getActivity() != null)
                getActivity().getmCamera().setPreviewCallback(previewCb);
        }else{
            if (getActivity() != null)
                getActivity().getmCamera().setPreviewCallback(null);
            barcodeScanned = true;
            scanner = null;
        }
    }



    Camera.PreviewCallback previewCb = new Camera.PreviewCallback()
    {
        public void onPreviewFrame(byte[] data, Camera camera)
        {

            try {

                generateBitmapIfGif(data, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
                previewCount++;
                //so verifica qr code X em X vezes
                if((previewCount % qrCodeSampleTime) != 0)
                    return;
                previewCount = 0;
                if(barcodeScanned)
                    return;
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);
                //Log.d("myapp", "*************************onPreviewFrame: " + result);
                if (result != 0 && !barcodeScanned)
                {
                    barcodeScanned = true;
                    //stop_camera();
                    SymbolSet syms = scanner.getResults();
                    Log.d("myapp", "*************************syms: " + syms.size());
                    for (Symbol sym : syms)
                    {



                        Bundle args = new Bundle();
                        args.putString(Global.IMAGE_RESOURCE_URL, "url");
                        args.putString(Global.TEXT_RESOURCE, "O que pensas do serviço prestado?");
                        args.putInt(Global.ID_RESOURCE, 5000);
                        args.putString(Global.OWNER_RESOURCE, "Empresa X");
                        args.putString(Global.DATE_RESOURCE, ReceivedPhoto.getTimeAgo("2016-02-20 14:30"));
//                        if(photos.get(position).isHasVoted())
//                            args.putString(Global.VOTE_DATE_RESOURCE, photos.get(position).getTimeAgo(photos.get(position).getVotedDate()));
                        args.putString(Global.MYCOMMENT_RESOURCE, "");

                        args.putBoolean(Global.HAS_VOTED_RESOURCE, false);
                        args.putInt(Global.CHOICE_RESOURCE, 0);
                        args.putBoolean(Global.IS_TEMPORARY_RESOURCE, false);


                        QRCodeDialogFragment newFragment = QRCodeDialogFragment.newInstance(args);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("qr_code");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        newFragment.show(ft, "qr_code");



                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.btn_shutter:
                if(handler != null && !isLongClickActive) {
                    Log.d("gif", "OnLongClickListener gif");
                    bitmapsGif.clear();
                    numFrames = 0;
                    isLongClickActive = true;

                    onTakePick(true);
                    getActivity().screenHistory.add(0, 0);
                    (activity).enableSwipe(false);
                    barcodeScanned = true;
                    if (getActivity() != null)
                        getActivity().getmCamera().setPreviewCallback(previewCb);
                    handler.post(gifRunnable);


                }
            break;
        }

        return true;
    }

    private void addFrameWhenAvailable(){
        newFrameIfAvailable = true;


    }


    private boolean newFrameIfAvailable = false;

    private void generateBitmapIfGif(byte[] bitmapdata, int width, int height){

        if(newFrameIfAvailable && numFrames < MAXFRAMES) {



            Bitmap originalBitmap = PhotoSave.getBitmapImageFromYUV(bitmapdata, width, height );






            Log.d("gif", "generateBitmapIfGif:"+numFrames);
            newFrameIfAvailable = false;
            Log.d("gif", "bitmapdata.length:"+bitmapdata.length);
//            Bitmap originalBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, null);
            Log.d("gif", "originalBitmap!=null:"+ (originalBitmap!=null));
            int orientation = Exif.getOrientation(bitmapdata);

            Log.d("gif", "orientation:"+ orientation);
            Log.d("gif", "width:"+ originalBitmap.getWidth());
            Log.d("gif", "height:"+ originalBitmap.getHeight());
            if(width > height)
                orientation = 90;
            switch(orientation) {
                case 90:
                    originalBitmap = PhotoSave.RotateBitmap(originalBitmap, 90);
                    break;
                case 270:
                    originalBitmap = PhotoSave.RotateBitmap(originalBitmap, 270);
                    break;

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d("gif", "originalBitmap.getAllocationByteCount():"+originalBitmap.getAllocationByteCount());
            }

//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//            originalBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            bitmapsGif.add(originalBitmap);

            FileOutputStream outStream = null;
            try {
                File file = PhotoSave.getOutputMediaFile();
                outStream = new FileOutputStream(file);
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream); // bmp is your Bitmap instance
                outStream.close();

                        photoPath = file.getAbsolutePath();
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            } catch (FileNotFoundException e) {
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e) {
                Log.d("CAMERA", e.getMessage());
            }




            Log.d("gif", "bitmapsGif.add(decoded):"+numFrames);
            if(numFrames == (MAXFRAMES -1))
                saveGifToFile();
            numFrames++;
        }

    }


//    public byte[] generateGIF() {
////        ArrayList<Bitmap> bitmaps = adapter.getBitmapArray();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
//        encoder.setDelay(1000);
//        encoder.start(bos);
//        for (Bitmap bitmap : bitmapsGif) {
//            encoder.addFrame(bitmap);
//        }
//        encoder.finish();
//        return bos.toByteArray();
//    }

    public void saveGifToFile(){
        Log.d("gif", "save gif");
        isLongClickActive = false;




        new SaveGifBackgroundTask(bitmapsGif, new SaveGifBackgroundTask.GifSaved() {
            @Override
            public void onGifSaved(Uri uri, String photoPath) {
                Log.d("gif", "onGifSaved");
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                Toast.makeText(AppController.getAppContext(), "Gif saved", Toast.LENGTH_SHORT);

                stop_camera();
                if (temp_pic != null) {
                    temp_pic.setVisibility(View.VISIBLE);
                }

                Glide.with(getActivity())
                .load(photoPath)
                .asGif()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                        Log.d("glide", "exception");
                        if(e != null)
                            Log.d("glide", "exception->"+e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d("glide", "onResourceReady");
                        return false;
                    }
                })
                .into(temp_pic);


            }
        }).execute();
    }

    public Camera getmCamera() {
        return mCamera;
    }
}
