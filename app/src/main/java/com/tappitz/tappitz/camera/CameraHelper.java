package com.tappitz.tappitz.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.background.BackgroundService;
import com.tappitz.tappitz.model.FutureUpload;
import com.tappitz.tappitz.model.SentPicture;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CreatePhotoService;
import com.tappitz.tappitz.ui.BlankFragment;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.ui.secondary.SelectContactFragment;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class CameraHelper implements View.OnClickListener {

    private ScreenSlidePagerActivity activity;
    private Button btn_shutter;
    private String photoPath;
    private ImageView temp_pic;
    private boolean turnLightOn = false;
    private View textMsgWrapper;
    public boolean requestedFile = false;
    RelativeLayout layout_after_photo, layout_before_photo;
    private EditText textMsg;
    private byte[] photoData;
    private String pictureBase64;

    final static int[] CLICABLES = {R.id.camera_options, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept, R.id.btnText};


    public CameraHelper(ScreenSlidePagerActivity act) {
        this.activity = act;

        btn_shutter = (Button) activity.findViewById(R.id.btn_shutter);
        photoPath = "";
        temp_pic = (ImageView) activity.findViewById(R.id.temp_pic);

        textMsgWrapper = activity.findViewById(R.id.textMsgWrapper);
        layout_after_photo = (RelativeLayout)activity.findViewById(R.id.layout_after_photo);
        layout_before_photo = (RelativeLayout)activity.findViewById(R.id.layout_before_photo);

        textMsg = (EditText)activity.findViewById(R.id.textMsg);

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
        if(width == 0) {

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
        textMsgWrapper.setVisibility(View.INVISIBLE);

//        temp_pic.setVisibility(View.VISIBLE);
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

        btn_shutter.setVisibility(View.GONE);

        showBtnOptions(true);
        Log.d("MyCameraApp", "layout_after_photo3:" + layout_after_photo.isShown());
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
            }
        });
        task.execute();
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            Log.d("myapp", "PictureCallback");
            photoData = data;
            stop_camera();
            //getActivity().destroyView();

            new SavePhotoBackgroundTask(photoData, new SavePhotoBackgroundTask.SaveNewRotatedPictureInterface() {
                @Override
                public void onSaveToFileRotated(Uri uri, String photoNewPath) {
                    //se houve erro e o uri estiver a null mostramos erro ao utilizador
                    if(uri == null ){
                        if(activity != null){
                            Toast.makeText(activity, "There was a problem with the picture try again.", Toast.LENGTH_LONG);
                        }
                    }else{

                        photoPath = UriPath.getPath(activity, uri);
                        loadBitmapFile(temp_pic, photoPath, AppController.getInstance().width, AppController.getInstance().height);
                        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        photoData = null;

                    }
                }
            }).execute();

        }
    };

    public void deletePrevious(){
        //recomeça a camera caso fosse foto da galeria

        textMsg.setText("");
        pictureBase64 = "";
        photoPath = "";
        photoData = null;
        start_camera();
        ( activity).enableSwipe(true);
        textMsgWrapper.setVisibility(View.INVISIBLE);

        recycleImagesFromView(temp_pic);
        onTakePick(false);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btn_shutter.getWindowToken(), 0);

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
            //surfaceView.setVisibility(View.VISIBLE);
        }
        //btn_shutter.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
        
        
        //showBtnOptions(!takePhoto);
        layout_before_photo.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
        layout_after_photo.setVisibility(takePhoto ? View.VISIBLE : View.GONE);

        Log.d("MyCameraApp", "layout_after_photo5:" + layout_after_photo.isShown());
    }

    public void showBtnOptions(boolean show){
        Log.d("myapp", "showBtnOptions:"+show);
//        activity.findViewById(R.id.camera_options).setVisibility(!show ? View.GONE : View.VISIBLE);
//        activity.findViewById(R.id.layout_camera).setVisibility(View.GONE);
//        rootView.findViewById(R.id.go_to).setVisibility(!show ? View.GONE : View.VISIBLE);
//        rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
    }

    private void showEditText(){
        Log.d("myapp", "showEditText");
        final boolean isVisible = textMsgWrapper.isShown();
        activity.findViewById( R.id.btnText).setEnabled(false);
        textMsgWrapper.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        activity.findViewById(R.id.btnText).setEnabled(true);

        if(!isVisible){
            textMsg.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textMsg, InputMethodManager.SHOW_IMPLICIT);
        }else {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btn_shutter.getWindowToken(), 0);
        }
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
                    Log.d("myapp", "btn_shutter2**");
                    getActivity().getmCamera().takePicture(null, null, mPicture);
                    Log.d("myapp", "btn_shutter2");
                    (activity).enableSwipe(false);
                    onTakePick(true);
                }
                break;

            case R.id.btnPhotoDelete:

                Log.d("myapp", "btnPhotoDelete");
                deletePrevious();

                break;
            case R.id.btnText:
                showEditText();
                Log.d("MyCameraApp", "layout_after_photo4:" + layout_after_photo.isShown());
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
                        Log.d("myapp", "btn_flash is null:");
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
                    Log.d("myapp", "btn_flash end:");
                    enableCameraButtons(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("myapp", "btn_flash Exception:");
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
//        if(pictureBase64 == null || pictureBase64.equals("")){
//            try {
//                InputStream inputStream = null;//You can get an inputStream using any IO API
//                inputStream = new FileInputStream(photoPath);
//                byte[] buffer = new byte[8192];
//                int bytesRead;
//
//                ByteArrayOutputStream output = new ByteArrayOutputStream();
//                Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
//                try {
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        output64.write(buffer, 0, bytesRead);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                output64.close();
//
//                pictureBase64 = output.toString();
//                pictureBase64 = pictureBase64.replace("\n","");
//                Log.d("myapp", "pictureBase64:" + pictureBase64);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


        final String comment = textMsgWrapper.isShown()? textMsg.getText().toString() : "";
        final SelectContactFragment newFragment = new SelectContactFragment();
        newFragment.setListener(new SelectContactFragment.OnSelectedContacts() {
            @Override
            public void sendPhoto(final List<Integer> contacts) {

                SentPicture out = new SentPicture(comment, photoPath);
                //actualiza e guarda offline
                if (getActivity().getUpdateAfterPicture() != null) {
                    getActivity().getUpdateAfterPicture().updateTemporaryOutbox(out);
                }
                //adiciona work futuro
                FutureUpload upload = out.generateFutureWork(contacts);
                BackgroundService.addPhotoUploadWork(upload);



                //progressDialog.dismiss();
                deletePrevious();

                //lançar o serviço
                AppController.getAppContext().startService(new Intent(AppController.getAppContext(), BackgroundService.class));
                newFragment.dismiss();
            }
        });
        newFragment.show(ft, "dialog");

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

}
