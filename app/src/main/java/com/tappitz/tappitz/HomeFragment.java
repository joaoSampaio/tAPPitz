package com.tappitz.tappitz;

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
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tappitz.tappitz.util.BitmapWorkerTask;
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
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button btn_shutter;
    private LinearLayout btn_layout;
    private LinearLayout camera_options;
    private boolean previewing = false;
    private String photoPath;
    private ImageView temp_pic;
    private boolean isLighOn = false;
    private int currentCameraId;
    private int viewWidth, viewHeight;
    private View textMsgWrapper;
    private boolean requestedFile = false;

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
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        textMsgWrapper = rootView.findViewById(R.id.textMsgWrapper);

        textMsgWrapper.setVisibility(View.INVISIBLE);
        btn_layout.setVisibility(View.INVISIBLE);
        temp_pic.setVisibility(View.VISIBLE);
        btn_shutter.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
        camera_options.setVisibility(View.VISIBLE);

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
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


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

        try
        {
            if(null != camera)
            {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_shutter:

                //animação do flash, é terminada qd a foto for tirada
                Animation animation = new AlphaAnimation(1, 0);
                animation.setDuration(50);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.REVERSE);
                surfaceView.startAnimation(animation);


                camera.takePicture(null, null, mPicture);

                onTakePick(true);
                break;

            case R.id.btnPhotoDelete:

                deletePrevious();
                recycleImagesFromView(temp_pic);
                //camera.startPreview();
                //onRetakePic();
                onTakePick(false);
                break;
            case R.id.btnText:
                //textMsgWrapper.setVisibility(textMsgWrapper.isShown()? View.GONE: View.VISIBLE);
                showEditText();
                break;
            case R.id.btnPhotoAccept:

                cameraReturn();
                break;

            case R.id.btn_load:

                stop_camera();
//                if (Build.VERSION.SDK_INT <19){
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Seleccionar ficheiro"),Global.BROWSE_REQUEST);
//                } else {
//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("image/*");
//                    startActivityForResult(intent, Global.BROWSE_REQUEST_KITKAT);
//                }

                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar ficheiro"), Global.BROWSE_REQUEST);




                break;
            case R.id.btn_flash:
                Camera.Parameters params = camera.getParameters();
                Button b = (Button)rootView.findViewById(R.id.btn_flash);

                if (isLighOn) {

                    Log.i("info", "torch is turn off!");

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    camera.startPreview();
                    isLighOn = false;
                    b.setTextColor(Color.BLACK);
                } else {

                    Log.i("info", "torch is turn on!");

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

                    camera.setParameters(params);
                    camera.startPreview();
                    isLighOn = true;
                    b.setTextColor(Color.YELLOW);

                }
                break;
            case R.id.btn_toggle_camera:
                if (previewing) {
                    camera.stopPreview();
                }
//NB: if you don't release the current camera before switching, you app will crash
                camera.release();

//swap the id of the camera to be used
                if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }

                start_camera();
                break;


        }
    }

    private void setUP(){


        ((MainActivity)getActivity()).displayTabs();
        if(!requestedFile) {
            Log.d("myapp", "**setUP*start_camera:");
            start_camera();
        }
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
//            if(!filePath.contains(".")){
//                filePath = getRealPathFromUri(getActivity(), selectedImageUri);
//
//            }
//            Log.d("myapp", "/////////filePath:" + filePath);

            //stop_camera();
            requestedFile = true;
            loadBitmapFile(temp_pic, filePath, viewWidth, viewHeight);
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
        try
        {
            if (previewing)
            {
                camera.stopPreview();
            }

            if(null != camera)
            {

                setCameraDisplayOrientation(getActivity(), currentCameraId, camera);
                Camera.Parameters params = camera.getParameters();
                if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else {
                    //Choose another supported mode
                }
                camera.setParameters(params);
                camera.startPreview();
                previewCamera();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        Camera.Parameters parameters = camera.getParameters();

        parameters.setRotation(degrees);
        parameters.set("orientation", "portrait");
        degrees = 90;
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            degrees = 270;
        parameters.set("rotation", degrees);
        camera.setParameters(parameters);
    }

    public void previewCamera()
    {
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            previewing = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }



    private Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            surfaceView.clearAnimation();
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
        //apaga a foto tirada ou recomeça a camera caso fosse foto da galeria
        surfaceView.setVisibility(View.GONE);
        if(!photoPath.equals("")) {
            File file = new File(photoPath);
            file.delete();
            photoPath = "";
        }else{
            start_camera();
        }
    }

    private void stop_camera()
    {
        if(!isCameraUsebyApp())
            return;
        camera.stopPreview();
        camera.release();
    }

    public boolean isCameraUsebyApp() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) camera.release();
        }
        return false;
    }

    private void start_camera()
    {
        if(isCameraUsebyApp())
            return;
        camera = Camera.open(currentCameraId);
        setCameraDisplayOrientation(getActivity(), currentCameraId, camera);
//        try {
//
//            camera.setPreviewDisplay(surfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        camera.startPreview();
        previewCamera();


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
        final TranslateAnimation anim_show = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_PARENT, show,
                Animation.RELATIVE_TO_PARENT, hide);
        anim_show.setDuration(ANIMATION_DURATION);
        anim_show.setFillAfter(true);

        anim_show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!isVisible)
                    textMsgWrapper.setVisibility(View.VISIBLE);
                Log.d("myapp", "onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isVisible)
                    textMsgWrapper.setVisibility(View.INVISIBLE);
                textMsgWrapper.setAnimation(null);
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
                if (isFirst)
                    camera_options.setVisibility(View.GONE);
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
                        if (!isFirst)
                            camera_options.setVisibility(View.VISIBLE);
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
