package com.tappitz.tappitz.ui.toBeRemoved;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.camera.BitmapWorkerTask;
import com.tappitz.tappitz.camera.CameraPreview;
import com.tappitz.tappitz.camera.ControlCameraTask;
import com.tappitz.tappitz.camera.SavePhotoBackgroundTask;
import com.tappitz.tappitz.camera.UriPath;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CreatePhotoService;
import com.tappitz.tappitz.ui.BlankFragment;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.ui.secondary.SelectContactFragment;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class HomeFragment2 extends Fragment implements View.OnClickListener, TextureView.SurfaceTextureListener {

    private Button btn_shutter;
    private String photoPath;
    private ImageView temp_pic;
    private boolean turnLightOn = false;
    private View textMsgWrapper;
    private boolean requestedFile = false;
    RelativeLayout layout_after_photo;
    private EditText textMsg;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private String pictureBase64;
    private int previewCount = 0;
    private int qrCodeSampleTime = 5;
    private boolean isPhotoMenuOpen;



    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private FrameLayout preview;
    private boolean previewing = true;
    private CameraHandlerThread mThread = null;

    private TextureView textureView;

    private byte[] photoData;

//    static {
//        System.loadLibrary("iconv");
//    }

    final static int[] CLICABLES = {R.id.camera_options, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept, R.id.btnText};

    View rootView;
    public HomeFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);

        Log.d("myapp", "onCreateView HOME2");
        btn_shutter = (Button) rootView.findViewById(R.id.btn_shutter);
        photoPath = "";
        temp_pic = (ImageView) rootView.findViewById(R.id.temp_pic);

        textMsgWrapper = rootView.findViewById(R.id.textMsgWrapper);
        layout_after_photo = (RelativeLayout)rootView.findViewById(R.id.layout_after_photo);
        textMsg = (EditText)rootView.findViewById(R.id.textMsg);
        isPhotoMenuOpen = false;

        textureView = (TextureView)rootView.findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);

        layout_after_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp2", "ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here

                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
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
            rootView.findViewById(id).setOnClickListener(this);

        btn_shutter.setOnClickListener(this);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            //nao funciona
            barcodeScanned = true;
        }else {
            barcodeScanned = true;
//            scanner = new ImageScanner();
//            scanner.setConfig(0, Config.X_DENSITY, 3);
//            scanner.setConfig(0, Config.Y_DENSITY, 3);

        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("myapp", "onResume");

        autoFocusHandler = new Handler();
       // start_camera();




        setUP();
        ((ScreenSlidePagerActivity)getActivity()).setCameraBackPressed(new ScreenSlidePagerActivity.CameraBackPressed() {
            @Override
            public boolean onBackPressed() {
                if (isPhotoMenuOpen) {
                    deletePrevious();
                    return false;
                }
                return true;
            }
        });

    }




    @Override
    public void onStop()
    {
        super.onStop();
//        recycleImagesFromView(temp_pic);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("myapp", "onPause");
        ((ScreenSlidePagerActivity)getActivity()).setCameraBackPressed(null);


        stop_camera();

        if (mThread != null && mThread.isAlive())
            mThread.interrupt();
    }


    @Override
    public void onClick(View v) {
        View view;
        switch (v.getId()){
            case R.id.camera_options:

                if(v.getTag() != null && v.getTag() instanceof String){
                    rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);
                    return;
                }
                view = rootView.findViewById(R.id.layout_camera);
                view.setVisibility(view.isShown()? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_shutter:

                Log.d("myapp", "btn_shutter");

                if(mCamera != null) {
                    Log.d("myapp", "btn_shutter2**");
                    mCamera.takePicture(null, null, mPicture);
                    Log.d("myapp", "btn_shutter2");
                    ((ScreenSlidePagerActivity) getActivity()).enableSwipe(false);
                    onTakePick(true);
                }
                break;

            case R.id.btnPhotoDelete:

                Log.d("myapp", "btnPhotoDelete");
                deletePrevious();

                break;
            case R.id.btnText:
                showEditText();

                break;
            case R.id.btnPhotoAccept:

                if(photoPath == null || photoPath.equals("")) {
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait", "We're saving the photo to file.", true);
                    new SavePhotoBackgroundTask(photoData, new SavePhotoBackgroundTask.SaveNewRotatedPictureInterface() {
                        @Override
                        public void onSaveToFileRotated(Uri uri, String photoNewPath) {

                            progressDialog.dismiss();

                            //se houve erro e o uri estiver a null mostramos erro ao utilizador
                            if(uri == null){
//                                Toast.makeText(, "There was a problem with the picture try again.", Toast.LENGTH_LONG);
                                cameraReturn();
                            }else{
                                photoPath = photoNewPath;
                                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                photoData = null;
                                cameraReturn();
                                showDialog();
                            }
                        }
                    }).execute();
                }else {
                    cameraReturn();
                    showDialog();
                }
                break;

            case R.id.btn_load:

                stop_camera();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar ficheiro"), Global.BROWSE_REQUEST);

                break;
            case R.id.btn_flash:
                Log.d("myapp", "btn_flash:");
                try {
                    enableCameraButtons(false);
                    if(mCamera == null) {
                        Log.d("myapp", "btn_flash is null:");
                        enableCameraButtons(true);
                        return;
                    }
                    Camera.Parameters params = mCamera.getParameters();
                    Button b = (Button)rootView.findViewById(R.id.btn_flash);

                    turnLightOn = !turnLightOn;
                    AppController.getInstance().turnLightOn = turnLightOn;

                    if (turnLightOn) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        mCamera.setParameters(params);
                        mCamera.startPreview();
                        b.setTextColor(Color.YELLOW);
                    } else {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                        mCamera.startPreview();
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
                stop_camera(new CallbackCamera() {
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

        BlankFragment.ButtonEnable listener = ((ScreenSlidePagerActivity) getActivity()).getButtonEnable();
        if(listener != null){
            listener.enableCameraButtons(enable);
        }
    }

    private void setUP(){
        Log.d("MyCameraApp", "setUP home");
        textMsgWrapper.setVisibility(View.INVISIBLE);
        temp_pic.setVisibility(View.VISIBLE);

        Display d = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        AppController.getInstance().width = width;
        AppController.getInstance().height = height;
        Log.d("MyCameraApp", "setUP home requestedFile:" + requestedFile);
        if(!requestedFile) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    start_camera();
                    showBtnOptions(false);
                    layout_after_photo.setVisibility(View.GONE);
                }
            }, 500);

        }else {
            onTakePick(true);
        }

        btn_shutter.setVisibility(View.GONE);

        showBtnOptions(true);
    }


    void showDialog() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        if(pictureBase64 == null || pictureBase64.equals("")){
            try {
                InputStream inputStream = null;//You can get an inputStream using any IO API
                inputStream = new FileInputStream(photoPath);
                byte[] buffer = new byte[8192];
                int bytesRead;

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output64.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output64.close();

                pictureBase64 = output.toString();
                pictureBase64 = pictureBase64.replace("\n","");
                Log.d("myapp", "pictureBase64:" + pictureBase64);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        final String comment = textMsgWrapper.isShown()? textMsg.getText().toString() : "";
        final SelectContactFragment newFragment = new SelectContactFragment();
        newFragment.setListener(new SelectContactFragment.OnSelectedContacts() {
            @Override
            public void sendPhoto(final List<Integer> contacts) {

                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Sending Photo", "Your photo is being uploaded", true);

                new CreatePhotoService(comment, contacts, pictureBase64, new CallbackMultiple<Integer, String>() {
                    @Override
                    public void success(Integer pictureId) {
                        if (getActivity() != null) {
                            ((ScreenSlidePagerActivity) getActivity()).enableSwipe(true);
                            Toast.makeText(getActivity(), "Photo sent", Toast.LENGTH_SHORT).show();

                            PhotoOutbox out = new PhotoOutbox(pictureId, comment);
                            if (((ScreenSlidePagerActivity) getActivity()).getUpdateAfterPicture() != null) {
//                                ((ScreenSlidePagerActivity) getActivity()).getUpdateAfterPicture().updateOutbox(out);
                            }
                        }
                        newFragment.dismiss();
                        progressDialog.dismiss();
                        deletePrevious();
                    }

                    @Override
                    public void failed(String error) {
                        if (getActivity() != null) {
                            ((ScreenSlidePagerActivity) getActivity()).enableSwipe(true);
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            newFragment.dismiss();
                            progressDialog.dismiss();
                            deletePrevious();
                        }
                    }
                }).execute();
            }
        });
        newFragment.show(ft, "dialog");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myapp", "onActivityResult: " + requestedFile);
        if(requestCode == Global.BROWSE_REQUEST && resultCode == Activity.RESULT_OK&& null != data) {
            Log.d("myapp", "onActivityResult dentro 1");
            stop_camera();
            ((ScreenSlidePagerActivity)getActivity()).enableSwipe(false);
            Uri selectedImageUri = data.getData();
            photoPath = UriPath.getPath(getActivity(), selectedImageUri);
            requestedFile = true;
            loadBitmapFile(temp_pic, photoPath, AppController.getInstance().width, AppController.getInstance().height);
            onTakePick(true);
        }else {
            requestedFile = false;
            ((ScreenSlidePagerActivity) getActivity()).enableSwipe(true);
        }
    }

    public void loadBitmapFile(ImageView imageView,String path, int width, int height) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, path, width, height);
        task.setListener(new BitmapWorkerTask.SaveNewRotatedPictureInterface() {
            @Override
            public void onSaveToFileRotated(Uri uri, String photoPathNew) {
                Log.d("myapp", "onSaveToFileRotated ");
                photoPath = photoPathNew;
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }
        });
        task.execute();
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            Log.d("myapp", "PictureCallback");
            photoData = data;
            stop_camera();



            new SavePhotoBackgroundTask(photoData, new SavePhotoBackgroundTask.SaveNewRotatedPictureInterface() {
                @Override
                public void onSaveToFileRotated(Uri uri, String photoNewPath) {



                    //se houve erro e o uri estiver a null mostramos erro ao utilizador
                    if(uri == null ){
                        if(getActivity() != null){
                            Toast.makeText(getActivity(), "There was a problem with the picture try again.", Toast.LENGTH_LONG);
                        }
                        cameraReturn();
                    }else{

                        photoPath = UriPath.getPath(getActivity(), uri);
                        loadBitmapFile(temp_pic, photoPath, AppController.getInstance().width, AppController.getInstance().height);
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        photoData = null;
//                                cameraReturn();
//                                showDialog();
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
        ((ScreenSlidePagerActivity) getActivity()).enableSwipe(true);
        textMsgWrapper.setVisibility(View.INVISIBLE);

        recycleImagesFromView(temp_pic);
        onTakePick(false);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }



    private void onCameraAvailable(){
        if(getActivity() != null) {
            Log.d("cam", "onCameraAvailable");
            ((ScreenSlidePagerActivity) getActivity()).callbackCameraAvailable();
            ((ScreenSlidePagerActivity) getActivity()).notifyCameraReady();
        }


        btn_shutter.setVisibility(View.VISIBLE);
        showBtnOptions(true);
//        try {
//            if(AppController.getInstance().mCamera != null){
//                Log.d("myapp", "setPreviewCallback: ");
//
//                //isto parte o tlm do Paulo!!!
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//                    //nao funciona
//                    AppController.getInstance().mCamera.setPreviewCallback(previewCb);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }





    public void cameraReturn(){
    }


    private void showEditText(){
        Log.d("myapp", "showEditText");
        final boolean isVisible = textMsgWrapper.isShown();
        rootView.findViewById( R.id.btnText).setEnabled(false);
        textMsgWrapper.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        rootView.findViewById(R.id.btnText).setEnabled(true);

        if(!isVisible){
            textMsg.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textMsg, InputMethodManager.SHOW_IMPLICIT);
        }else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }

    private void showBtnOptions(boolean show){
        rootView.findViewById(R.id.camera_options).setVisibility(!show ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);
//        rootView.findViewById(R.id.go_to).setVisibility(!show ? View.GONE : View.VISIBLE);
//        rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
    }

    private void onTakePick(final boolean takePhoto)
    {
        Log.d("myapp", "onTakePick " + takePhoto);
        //este metodo esconde o menu da camera ou mostra o botao para tirar foto, simplesmente tem animações porque era codigo que ja tinha feito para outra app
        if(!takePhoto){
            temp_pic.setVisibility(View.GONE);
            //surfaceView.setVisibility(View.VISIBLE);
        }
        isPhotoMenuOpen = takePhoto;
        btn_shutter.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
        showBtnOptions(!takePhoto);
        layout_after_photo.setVisibility(takePhoto ? View.VISIBLE : View.GONE);
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

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback()
    {
        public void onPreviewFrame(byte[] data, Camera camera)
        {

            try {
                if(barcodeScanned)
                    return;

                previewCount++;
                //so verifica qr code X em X vezes
                if((previewCount % qrCodeSampleTime) != 0)
                    return;
                previewCount = 0;
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                Image barcode = new Image(size.width, size.height, "Y800");

                barcode.setData(data);
                int result = scanner.scanImage(barcode);
                //Log.d("myapp", "*************************onPreviewFrame: " + result);
                if (result != 0)
                {
                    barcodeScanned = true;
                    //stop_camera();
                    SymbolSet syms = scanner.getResults();
                    Log.d("myapp", "*************************syms: " + syms.size());
                    for (Symbol sym : syms)
                    {
                        barcodeScanned = true;

                        showToast("Encontrou QR code!");
                        //Toast.makeText(getActivity(), "Encontrou QR code!", Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("qr code");
                        alertDialog.setMessage("data: " + sym.getData());

                        alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // here you can add functions
                                barcodeScanned = false;
                            }
                        });
                        alertDialog.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    public void showToast(String msg){
        try {
            if(getActivity() != null)
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void stop_camera() {

//
//        Log.d("Cam", "releaseCamera " + (mCamera != null));
//        if (mCamera != null) {
//            previewing = false;
//            mCamera.stopPreview();
//            mCamera.setPreviewCallback(null);
//            mPreview.getHolder().removeCallback(mPreview);
//            mCamera.release();
//            mCamera = null;
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    preview.removeAllViews();
//                }
//            });
//
//        }
    }

    private void stop_camera(CallbackCamera call) {
//        Log.d("Cam", "openCamera " + (mCamera != null) + "mThread:" + (mThread != null));
//        if (mThread == null) {
//            try {
//                mThread = new CameraHandlerThread("blabla");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        synchronized (mThread) {
//            mThread.setCallback(call);
//            mThread.closeCamera();
//        }
    }


    private void start_camera() {
//        Log.d("Cam", "openCamera " + (mCamera != null) + "mThread:" + (mThread != null));
//        if (mThread == null) {
//            try {
//                mThread = new CameraHandlerThread("blabla");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        synchronized (mThread) {
//            mThread.setCallback(new CallbackCamera() {
//                @Override
//                public void onDone() {
//                    onCameraAvailable();
//                }
//
//                @Override
//                public void onError() {
//
//                }
//            });
//            mThread.openCamera();
//        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("Cam", "onSurfaceTextureAvailable");

        mCamera = Camera.open();

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            Log.d("Cam", "IOException");
            // Something bad happened
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d("Cam", "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("Cam", "onSurfaceTextureDestroyed");
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
// Invoked every time there's a new Camera preview frame
    }


    public interface CallbackCamera {
        public void onDone();
        public void onError();
    }


    private class CameraHandlerThread extends HandlerThread implements Camera.AutoFocusCallback, Camera.PreviewCallback {

        Handler mHandler = null;
        private CallbackCamera callback;

        public CameraHandlerThread(String name) throws InterruptedException {
            super(name);
//            callBack.showProgressDialog(true);
            start();

            mHandler = new Handler(getLooper());
        }

        public void setCallback(CallbackCamera callback) {
            this.callback = callback;
        }

        void openCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

//                    try {
//                        Thread.sleep(550);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    if(mCamera != null){
                        Log.d("Cam", "A camera estava aberta no start");
                        previewing = false;
                        mCamera.stopPreview();
                        mCamera.setPreviewCallback(null);
                        if(mPreview != null && mPreview.getHolder() != null)
                            mPreview.getHolder().removeCallback(mPreview);
                        mCamera.release();
                        mCamera = null;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                preview.removeAllViews();
                            }
                        });
                    }
                    mCamera = null;
                    try {

                        if(AppController.getInstance().currentCameraId != Camera.CameraInfo.CAMERA_FACING_FRONT &&
                                AppController.getInstance().currentCameraId !=  Camera.CameraInfo.CAMERA_FACING_BACK){
                            Log.d("Cam", "openCamera CAMERA_FACING_FRONT");
                            AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        }

                        Log.d("Cam", "openCamera 1");
                        mCamera = Camera.open(AppController.getInstance().currentCameraId);
                        mCamera.setErrorCallback(new Camera.ErrorCallback() {
                            @Override
                            public void onError(int error, Camera camera) {
                                Log.d("Cam", "openCamera error:"+ error + " ");
                                try {
                                    Thread.sleep(250);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Log.d("Cam", "Trying again");
                                closeCamera();
                                openCamera();
                            }
                        });
                        setCameraDisplayOrientation(AppController.getInstance().currentCameraId, mCamera);
                        Log.d("Cam", "openCamera 2");
                    } catch (final Exception e) {
                        Log.d("BarcodeFinderFragment", e.toString());
//                        callBack.setActivityResult(Activity.RESULT_CANCELED, null);
                        interrupt();
                    }
                    notifyCameraOpened();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Cam", "runOnUiThread 1");
                            mPreview = new CameraPreview(getActivity(), mCamera);
                            Log.d("Cam", "runOnUiThread 2");
                            preview.addView(mPreview);
                            Log.d("Cam", "runOnUiThread 3");
//                            rootView.findViewById(R.id.camera_buttons).bringToFront();

//                            mPreview = new CamViewFinder(getActivity(), mCamera, CameraHandlerThread.this, CameraHandlerThread.this);
//                            preview.addView(mPreview);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("Cam", "callback.onDone(); ");
                                    callback.onDone();
//                                    callBack.showProgressDialog(false);
                                }
                            }, 500);
                        }
                    });
                }
            });
        }

        void closeCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("Cam", "closeCamera 1");
                        if(mCamera != null) {
                            Log.d("Cam", "closeCamera 2");
                            previewing = false;
                            mCamera.stopPreview();
                            mCamera.setPreviewCallback(null);
                            mPreview.getHolder().removeCallback(mPreview);
                            mCamera.release();
                            mCamera = null;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    preview.removeAllViews();
                                }
                            });
                        }

                    } catch (final Exception e) {
                        Log.d("BarcodeFinderFragment", e.toString());
//                        callBack.setActivityResult(Activity.RESULT_CANCELED, null);
                        interrupt();
                    }
                    //notifyCameraOpened();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone();
//                                    callBack.showProgressDialog(false);
                                }
                            }, 100);
                        }
                    });
                }
            });
        }

        synchronized void notifyCameraOpened() {
            notify();

        }

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (previewing) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCamera.autoFocus(CameraHandlerThread.this);
                            }
                        });
                    }
                }
            }, 1000);
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            final Camera.Parameters parameters = camera.getParameters();
            final Camera.Size size = parameters.getPreviewSize();

            final Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            final int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                final SymbolSet syms = scanner.getResults();
                for (final Symbol sym : syms) {
                    final Bundle bundle = new Bundle();
                    bundle.putString("result", sym.getData());
                    bundle.putString("codeType", "" + sym.getType());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            callBack.setActivityResult(Activity.RESULT_OK, bundle);
                        }
                    });
                }
            }
        }


        public  void setCameraDisplayOrientation(
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



    }


}
