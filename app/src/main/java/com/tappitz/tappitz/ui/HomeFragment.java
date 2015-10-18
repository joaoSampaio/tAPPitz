package com.tappitz.tappitz.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.tappitz.tappitz.util.BitmapWorkerTask;
import com.tappitz.tappitz.util.ControlCameraTask;
import com.tappitz.tappitz.util.UriPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {

    private final int ANIMATION_DURATION = 500;
    private final int MEDIA_TYPE_IMAGE = 1;
    private final int MEDIA_TYPE_VIDEO = 2;
    //private Camera camera;
    private SurfaceView surfaceView;
    //private SurfaceHolder surfaceHolder;
    private Button btn_shutter;
    private LinearLayout btn_layout;
    private LinearLayout camera_options;
    private boolean previewing = false;
    private String photoPath;
    private ImageView temp_pic;
    private boolean isLighOn = false;
    //private int currentCameraId;
    private int viewWidth, viewHeight;
    private View textMsgWrapper;
    private boolean requestedFile = false;
    RelativeLayout whiteBackground;
    private EditText textMsg;

    final static int[] CLICABLES = {R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept, R.id.btnText};


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
        btn_layout = (LinearLayout) rootView.findViewById(R.id.btn_layout);
        camera_options = (LinearLayout) rootView.findViewById(R.id.camera_options);
        photoPath = "";
        temp_pic = (ImageView) rootView.findViewById(R.id.temp_pic);

        //AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

        textMsgWrapper = rootView.findViewById(R.id.textMsgWrapper);
        whiteBackground = (RelativeLayout)rootView.findViewById(R.id.whiteBackground);
        textMsg = (EditText)rootView.findViewById(R.id.textMsg);




        whiteBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:

                        //=====Write down your Finger Pressed code here
                        whiteBackground.setVisibility(View.INVISIBLE);
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);


                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        whiteBackground.setVisibility(View.VISIBLE);
                        //=====Write down you code Finger Released code here
                        if(textMsgWrapper.isShown()){
                            imm.showSoftInput(textMsg, InputMethodManager.SHOW_IMPLICIT);
                        }
                        return true;
                }

                return false;
            }
        });


        View v = rootView.findViewById(R.id.container);
        v.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        int w = v.getWidth();
        int h = v.getHeight();
        viewWidth = v.getMeasuredWidth();
        viewHeight = v.getMeasuredHeight();


        Log.d("myapp","***width:"+ viewWidth + " height:" + viewHeight);
        for(int id: CLICABLES)
            rootView.findViewById(id).setOnClickListener(this);

        btn_shutter.setOnClickListener(this);

        try
        {
            AppController.getInstance().surfaceHolder = surfaceView.getHolder();
            //surfaceHolder = surfaceView.getHolder();
            //surfaceHolder.addCallback(this);
            //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("myapp", "onResume");
        setUP();

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
        stop_camera();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_shutter:

                AppController.getInstance().mCamera.takePicture(null, null, mPicture);

                onTakePick(true);
                break;

            case R.id.btnPhotoDelete:

                textMsgWrapper.setVisibility(View.INVISIBLE);
                deletePrevious();
                recycleImagesFromView(temp_pic);
                //camera.startPreview();
                //onRetakePic();
                onTakePick(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                textMsg.setText("");

                break;
            case R.id.btnText:
                showEditText();

                break;
            case R.id.btnPhotoAccept:

                cameraReturn();
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

                if (isLighOn) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    AppController.getInstance().mCamera.setParameters(params);
                    AppController.getInstance().mCamera.startPreview();
                    previewing = true;
                    isLighOn = false;
                    b.setTextColor(Color.WHITE);
                } else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    AppController.getInstance().mCamera.setParameters(params);
                    AppController.getInstance().mCamera.startPreview();
                    isLighOn = true;
                    previewing = true;
                    b.setTextColor(Color.YELLOW);
                }
                break;
            case R.id.btn_toggle_camera:
                Log.d("myapp", "/////////btn_toggle_camera:" );
                if(AppController.getInstance().currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                Log.d("myapp", "///////// btn_toggle_camera end:");

                stop_camera(new ControlCameraTask.CallbackCamera() {
                    @Override
                    public void onDone() {
                        start_camera();
                    }
                });
                break;
        }
    }

    private void setUP(){
        Log.d("MyCameraApp", "setUP ");
        Log.d("MyCameraApp", "requestedFile " + requestedFile);
        textMsgWrapper.setVisibility(View.INVISIBLE);
        btn_layout.setVisibility(View.INVISIBLE);
        temp_pic.setVisibility(View.VISIBLE);
//        btn_shutter.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
//        camera_options.setVisibility(View.VISIBLE);
        whiteBackground.setVisibility(View.GONE);


        if(!requestedFile) {
            start_camera();

        }

        if(getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).displayTabs(true);


        camera_options.setVisibility(View.GONE);
        btn_shutter.setVisibility(View.GONE);
        Display d = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        AppController.getInstance().width = width;
        AppController.getInstance().height = height;
        Log.d("MyCameraApp", "setUP width: " + width + " height: " + height);
//setUP width: 720 height: 1184
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Global.BROWSE_REQUEST && resultCode == Activity.RESULT_OK&& null != data) {
            stop_camera();
            Uri selectedImageUri = data.getData();
            String filePath = UriPath.getPath(getActivity(), selectedImageUri);
            Log.d("myapp", "/////////selectedImageUri.toString():" + selectedImageUri.toString());
           // String filePath = selectedImageUri.getPath();
            Log.d("myapp", "/////////filePath before:" + filePath);

            requestedFile = true;
            loadBitmapFile(temp_pic, filePath, AppController.getInstance().width, AppController.getInstance().height);
            onTakePick(true);
        }else{
            start_camera();
        }


    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void loadBitmapFile(ImageView imageView,String path, int width, int height) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, path, width, height);
        task.execute();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            FileOutputStream outStream = null;
            try {
                File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                outStream = new FileOutputStream(file);
                outStream.write(data);
                outStream.close();

                photoPath = file.getAbsolutePath();
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            } catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }

        }
    };

    public void deletePrevious(){
        //recomeça a camera caso fosse foto da galeria
        new ControlCameraTask().execute(true);
    }

    private void stop_camera(ControlCameraTask.CallbackCamera callback){
        previewing = false;
        ControlCameraTask c = new ControlCameraTask();
        c.setCallback(callback);
        c.execute(false);
    }

    private void stop_camera()
    {
        previewing = false;
        new ControlCameraTask().execute(false);
    }


    private void waitForCamera(){
        btn_shutter.setVisibility(View.GONE);
        camera_options.setVisibility(View.GONE);
    }

    private void onCameraAvailable(){
        Long pastTime = System.currentTimeMillis() - currentTime;
        Log.d("MyCameraApp", "onCameraAvailable: " + pastTime);
        Toast.makeText(getContext(), pastTime + " Miliseconds", Toast.LENGTH_LONG).show();
        previewing = true;
        btn_shutter.setVisibility(View.VISIBLE);
        camera_options.setVisibility(View.VISIBLE);
    }

    Long currentTime;
    private void start_camera()
    {
        currentTime = System.currentTimeMillis();
        Log.d("MyCameraApp", "start_camera");
        waitForCamera();
        ControlCameraTask c = new ControlCameraTask();
        c.setCallback(new ControlCameraTask.CallbackCamera() {
            @Override
            public void onDone() {
                onCameraAvailable();
            }
        });
        c.execute(true);
    }

    @SuppressLint("SimpleDateFormat") private File getOutputMediaFile(int type)
    {
        if (true == isExternalStoragePresent())
        {
            //MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera");

            if (!mediaStorageDir.exists())
            {
                if (!mediaStorageDir.mkdirs())
                {
                    Log.d("MyCameraApp", "failed to create directory");
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;

            if (type == MEDIA_TYPE_IMAGE)
            {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            }
            else if (type == MEDIA_TYPE_VIDEO)
            {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            }
            else
            {
                return null;
            }

            return mediaFile;
        }
        else
        {
            try
            {
                new AlertDialog.Builder(getActivity()).setMessage("No storage space found, can't save the video.").setPositiveButton("Ok", null).show();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    public boolean isExternalStoragePresent()
    {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        }
        else
        {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if (false == ((mExternalStorageAvailable) && (mExternalStorageWriteable)))
        {
             Toast.makeText(getActivity(), "SD card not present", Toast.LENGTH_LONG);
        }

        return (mExternalStorageAvailable) && (mExternalStorageWriteable);
    }

    public void cameraReturn(){
        Log.d("myapp", "************cameraReturn");
//        String[] allPath = new String[photoPath.size()];
//        for (int i = 0; i < allPath.length; i++) {
//            allPath[i] = photoPath.get(i);
//        }
//
//        Intent data = new Intent().putExtra("all_path", allPath);
//        setResult(RESULT_OK, data);
//        finish();
    }

    private void showEditText(){
        Log.d("myapp", "showEditText");
        final boolean isVisible = textMsgWrapper.isShown();
        int show = isVisible? 0 : -1;
        int hide = isVisible? -1 : 0;

        rootView.findViewById( R.id.btnText).setEnabled(false);


        if(!isVisible){
            textMsg.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textMsg, InputMethodManager.SHOW_IMPLICIT);
        }else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }


        final TranslateAnimation anim_show = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_PARENT, show,
                Animation.RELATIVE_TO_PARENT, hide);
        anim_show.setDuration(ANIMATION_DURATION);
        anim_show.setFillAfter(true);

        anim_show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!isVisible) {
                    textMsgWrapper.setVisibility(View.VISIBLE);

                }
                Log.d("myapp", "onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isVisible) {
                    textMsgWrapper.setVisibility(View.INVISIBLE);
                }
                textMsgWrapper.setAnimation(null);
                rootView.findViewById( R.id.btnText).setEnabled(true);
                Log.d("myapp", "onAnimationEnd");
            }
        });
        textMsgWrapper.startAnimation(anim_show);
    }

    private void onTakePick(final boolean isFirst)
    {
        //este metodo esconde o menu da camera ou mostra o botao para tirar foto, simplesmente tem animações porque era codigo que ja tinha feito para outra app
        if(!isFirst){
            temp_pic.setVisibility(View.GONE);
            //start_camera();
            surfaceView.setVisibility(View.VISIBLE);
        }


        final TranslateAnimation anim_hide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1);
        anim_hide.setDuration(ANIMATION_DURATION);
        anim_hide.setFillAfter(true);

        final View view1 = isFirst? btn_shutter : btn_layout;
        final View view2 = isFirst? btn_layout : btn_shutter ;

        anim_hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view1.setVisibility(View.GONE);
                view1.setAnimation(null);
                if (isFirst) {
                    camera_options.setVisibility(View.GONE);
                    whiteBackground.setVisibility(View.VISIBLE);
                    ((MainActivity)getActivity()).displayTabs(false);
                }
                final TranslateAnimation anim_show = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_PARENT, 1,
                        Animation.RELATIVE_TO_PARENT, 0);
                anim_show.setDuration(ANIMATION_DURATION);
                anim_show.setFillAfter(true);

                anim_show.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        view2.setVisibility(View.VISIBLE);
                        if (!isFirst) {
                            camera_options.setVisibility(View.VISIBLE);
                            whiteBackground.setVisibility(View.GONE);
                            ((MainActivity)getActivity()).displayTabs(true);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view2.setAnimation(null);
                    }
                });
                view2.startAnimation(anim_show);
            }
        });

        view1.startAnimation(anim_hide);
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




}
