package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.camera.BitmapWorkerTask;
import com.tappitz.tappitz.camera.ControlCameraTask;
import com.tappitz.tappitz.camera.PhotoSave;
import com.tappitz.tappitz.camera.SavePhotoBackgroundTask;
import com.tappitz.tappitz.camera.UriPath;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.CreatePhotoService;
import com.tappitz.tappitz.ui.secondary.SelectContactFragment;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class HomeFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView surfaceView;
    private Button btn_shutter;
    private String photoPath;
    private ImageView temp_pic;
    private boolean turnLightOn = false;
    private View textMsgWrapper;
    private boolean requestedFile = false;
    RelativeLayout whiteBackground;
    private EditText textMsg;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private String pictureBase64;
    private int previewCount = 0;
    private int qrCodeSampleTime = 5;
    private boolean isPhotoMenuOpen;

    private byte[] photoData;

//    static {
//        System.loadLibrary("iconv");
//    }

    final static int[] CLICABLES = {R.id.camera_options, R.id.go_to, R.id.btn_goto_in, R.id.btn_goto_out, R.id.btn_goto_friends, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept, R.id.btnText};

    View rootView;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);


        Log.d("myapp", "onCreateView");
        surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceview);
        btn_shutter = (Button) rootView.findViewById(R.id.btn_shutter);
        photoPath = "";
        temp_pic = (ImageView) rootView.findViewById(R.id.temp_pic);

        textMsgWrapper = rootView.findViewById(R.id.textMsgWrapper);
        whiteBackground = (RelativeLayout)rootView.findViewById(R.id.whiteBackground);
        textMsg = (EditText)rootView.findViewById(R.id.textMsg);
        isPhotoMenuOpen = false;


        whiteBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp2", "ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here

                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        whiteBackground.setVisibility(View.INVISIBLE);

                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp2", "ACTION_UP");
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("myapp2", "ACTION_POINTER_UP");
                        whiteBackground.setVisibility(View.VISIBLE);
                        whiteBackground.bringToFront();
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

        try
        {
            AppController.getInstance().surfaceHolder = surfaceView.getHolder();
            AppController.getInstance().surfaceHolder.addCallback(this);
        }
        catch (Exception e)
        {
            Log.d("MyCameraApp","***AppController.getInstance().surfaceHolder = surfaceView.getHolder(); erro");
            e.printStackTrace();
        }

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
        //((ScreenSlidePagerActivity) getActivity()).enableSwipe(true);
        Log.d("myapp", "onResume");
        setUP();
        ((ScreenSlidePagerActivity)getActivity()).setCameraBackPressed(new ScreenSlidePagerActivity.CameraBackPressed() {
            @Override
            public boolean onBackPressed() {
                if(isPhotoMenuOpen) {
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
        recycleImagesFromView(temp_pic);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("myapp", "onPause");
        stop_camera();
        ((ScreenSlidePagerActivity)getActivity()).setCameraBackPressed(null);
    }


    @Override
    public void onClick(View v) {
        View view;
        switch (v.getId()){
            case R.id.camera_options:

                if(v.getTag() != null && v.getTag() instanceof String){
                    rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);
                    return;
                }


                view = rootView.findViewById(R.id.layout_camera);
                view.setVisibility(view.isShown()? View.GONE : View.VISIBLE);

                rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
                break;
            case R.id.go_to:
                view = rootView.findViewById(R.id.layout_goto);
                view.setVisibility(view.isShown()? View.GONE : View.VISIBLE);

                rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);
                break;
            case R.id.btn_goto_in:
                ((ScreenSlidePagerActivity)getActivity()).showPage(0);
                break;
            case R.id.btn_goto_out:
                ((ScreenSlidePagerActivity)getActivity()).showPage(2);
                break;
            case R.id.btn_goto_friends:
                ((ScreenSlidePagerActivity)getActivity()).showFriends();
                break;
            case R.id.btn_shutter:

                Log.d("myapp", "btn_shutter");

                if(AppController.getInstance().mCamera != null) {
                    Log.d("myapp", "btn_shutter2**");
                    AppController.getInstance().mCamera.takePicture(null, null, mPicture);
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
                            photoPath = photoNewPath;
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            photoData = null;
                            cameraReturn();
                            showDialog();
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
                Camera.Parameters params = AppController.getInstance().mCamera.getParameters();
                Button b = (Button)rootView.findViewById(R.id.btn_flash);

                turnLightOn = !turnLightOn;
                AppController.getInstance().turnLightOn = turnLightOn;

                if (turnLightOn) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    AppController.getInstance().mCamera.setParameters(params);
                    AppController.getInstance().mCamera.startPreview();
                    b.setTextColor(Color.YELLOW);
                } else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    AppController.getInstance().mCamera.setParameters(params);
                    AppController.getInstance().mCamera.startPreview();
                    b.setTextColor(Color.WHITE);
                }

                break;
            case R.id.btn_toggle_camera:

                PackageManager pm = getActivity().getPackageManager();

                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {

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
                        start_camera();
                    }

                    @Override
                    public void onError() {
                        stop_camera();
                        start_camera();
                    }
                });
                break;

        }
    }

    private void setUP(){
        Log.d("MyCameraApp", "setUP home");
        textMsgWrapper.setVisibility(View.INVISIBLE);
        temp_pic.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.VISIBLE);

        Display d = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        AppController.getInstance().width = width;
        AppController.getInstance().height = height;
        Log.d("MyCameraApp", "setUP home requestedFile:" +requestedFile);
        if(!requestedFile) {
            start_camera();
            showBtnOptions(false);
            whiteBackground.setVisibility(View.GONE);
        }else {
            onTakePick(true);
        }

        btn_shutter.setVisibility(View.GONE);
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

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Sending Photo", "Your photo is being uploaded", true);

                new CreatePhotoService(comment, contacts, pictureBase64, new CallbackMultiple<Integer, String>() {
                    @Override
                    public void success(Integer pictureId) {
                        if (getActivity() != null) {
                            ((ScreenSlidePagerActivity) getActivity()).enableSwipe(true);
                            Toast.makeText(getActivity(), "Photo sent", Toast.LENGTH_SHORT).show();

                            PhotoOutbox out = new PhotoOutbox(pictureId, comment);
                            if (((ScreenSlidePagerActivity) getActivity()).getUpdateAfterPicture() != null) {
                                ((ScreenSlidePagerActivity) getActivity()).getUpdateAfterPicture().updateOutbox(out);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("MyCameraApp", "surfaceCreated :" + requestedFile);
        if(!requestedFile) {
            Log.d("MyCameraApp", "surfaceChanged requestedFile:" + requestedFile);
            start_camera();
            showBtnOptions(false);
            whiteBackground.setVisibility(View.GONE);
        }
        requestedFile = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.d("MyCameraApp", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop_camera();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            Log.d("myapp", "PictureCallback");
            photoData = data;
            stop_camera();
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

    private void stop_camera(ControlCameraTask.CallbackCamera callback){
        ControlCameraTask c = new ControlCameraTask();
        c.setCallback(callback);
        c.execute(false);
    }

    private void stop_camera()
    {
        new ControlCameraTask().execute(false);
    }


    private void waitForCamera(){
        btn_shutter.setVisibility(View.GONE);
        showBtnOptions(false);
    }

    private void onCameraAvailable(){
        if(getActivity() != null) {
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

    private void start_camera()
    {
        Log.d("MyCameraApp", "start_camera");
        waitForCamera();
        ControlCameraTask c = new ControlCameraTask();
        c.setCallback(new ControlCameraTask.CallbackCamera() {
            @Override
            public void onDone() {
                onCameraAvailable();
            }

            @Override
            public void onError() {
            }
        });
        c.execute(true);
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
        rootView.findViewById(R.id.go_to).setVisibility(!show ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
    }

    private void onTakePick(final boolean takePhoto)
    {
        Log.d("myapp", "onTakePick " + takePhoto);
        //este metodo esconde o menu da camera ou mostra o botao para tirar foto, simplesmente tem animações porque era codigo que ja tinha feito para outra app
        if(!takePhoto){
            temp_pic.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
        }
        isPhotoMenuOpen = takePhoto;
        btn_shutter.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
        showBtnOptions(!takePhoto);
        whiteBackground.setVisibility(takePhoto ? View.VISIBLE : View.GONE);
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


}
