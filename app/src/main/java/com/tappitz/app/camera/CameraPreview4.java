package com.tappitz.app.camera;

import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tappitz.app.Global;
import com.tappitz.app.app.AppController;
import com.tappitz.app.model.CameraFrame;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.ui.MainActivity;
import com.tappitz.app.ui.secondary.QRCodeDialogFragment;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.util.List;

/**
 * Created by Sampaio on 05/05/2016.
 */
public class CameraPreview4 extends ViewGroup implements SurfaceHolder.Callback, CameraInterface{
    private static final double ASPECT_RATIO = 9.0 / 16.0;
    private final String TAG = "Preview";
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize, mPictureSize;
    List<Size> mSupportedPreviewSizes, mSupportedPictureSizes;
    Camera mCamera;
    MainActivity activity;

    private ImageScanner scanner;
    private boolean isCreated = false, barcodeScanned = false, captureFrame = false;
    private long timeOld = 0, timeCurrent = 0;
    private byte[] frameData;
    private CallbackCameraAction callbackCameraOpen;

    static {
        System.loadLibrary("iconv");
    }

    public CameraPreview4( MainActivity activity, CallbackCameraAction callbackCameraOpen) {
        super(activity);
        this.activity = activity;
        this.callbackCameraOpen = callbackCameraOpen;
        mSurfaceView = new SurfaceView(activity);
        addView(mSurfaceView);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
            requestLayout();

        }
        Log.d(TAG, "mCamera:"+(mCamera != null));
        Log.d(TAG, "isCreated:"+(isCreated));
        if(isCreated){
            requestLayout();
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(mHolder);
                    surfaceChangedCamera();
                }
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
//        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        Pair<Integer, Integer> sizes = getScreenSize();
        int width = sizes.first;
        int height = sizes.second;



        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, width, height);
        }
    }

    private Pair<Integer, Integer> getScreenSize(){
        Display display = activity.getWindowManager().getDefaultDisplay();

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            Log.d("preview", "navigation_bar_height before height:"+height + " after height:" + (height + resources.getDimensionPixelSize(resourceId)));
            height =  height + resources.getDimensionPixelSize(resourceId);
        }
        Log.d("preview", "getSize w:"+width + " height:" + height);
        return new Pair<Integer, Integer>(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

             int width = r - l;
             int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }
            if(previewWidth > previewHeight) {
                int tmpWidth = previewWidth;
                previewWidth = previewHeight;
                previewHeight = tmpWidth;
            }
            Log.d(TAG, "onLayout previewWidth:"+previewWidth + " preview previewHeight:"+previewHeight);
            // Center the child SurfaceView within the parent.
            if (width * previewHeight < height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }else{

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       startCamera();
                   }
               },200);



            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        isCreated = true;
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        if(h > w){
            int tmpWidth = w;
            w = h;
            h = tmpWidth;
        }
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d(TAG, "width:"+optimalSize.width + " height:"+optimalSize.height);
        return optimalSize;
    }

    private boolean reachedSurfaceChanged = false;

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Log.d(TAG, " surfaceChanged)");
        reachedSurfaceChanged = true;
        if(mCamera != null){
            surfaceChangedCamera();
        }

    }


    private void surfaceChangedCamera(){
        Camera.Parameters parameters = mCamera.getParameters();
        if (mPreviewSize == null) {
            Pair<Integer, Integer> sizes = getScreenSize();
            int width = sizes.first;
            int height = sizes.second;
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        if (mPictureSize == null) {
            Pair<Integer, Integer> sizes = getScreenSize();
            int width = sizes.first;
            int height = sizes.second;
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, width, height);
        }


        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        requestLayout();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    public Camera getmCamera() {
        return mCamera;
    }

    public void startCamera(){
        Log.d("MyCameraApp", "start camera .....>>>>>:");

        try {
            mCamera = Camera.open(AppController.getInstance().currentCameraId);
            determineDisplayOrientation();
            AppController.getInstance().mCameraReady = true;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
            requestLayout();
            mCamera.setPreviewDisplay(mHolder);


            if(reachedSurfaceChanged)
                surfaceChangedCamera();

            callbackCameraOpen.onSuccess();

        } catch (Exception exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            callbackCameraOpen.onFailure();
        }

    }




    public void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(AppController.getInstance().currentCameraId, cameraInfo);

        int rotation = (getActivity()).getWindowManager().getDefaultDisplay().getRotation();
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


        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            //Choose another supported mode
        }

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

    public void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    public void restartPreview(){
        if(getmCamera() != null ){

            getmCamera().startPreview();

        }
    }

    public MainActivity getActivity() {
        return activity;
    }

    @Override
    public void takePhoto(Camera.PictureCallback mPicture, CallbackCameraAction callback) {
        if(mCamera != null) {
            getmCamera().takePicture(null, null, mPicture);
            callback.onSuccess();
        }
    }

    @Override
    public void turnCamera(CallbackCameraAction callback) {
        Log.d("MyCameraApp", "turnCamera:");
        try {
            if(mCamera != null) {
                if(AppController.getInstance().currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    AppController.getInstance().currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                stopCamera();
                startCamera();
                callback.onSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    @Override
    public void toggleFlash(CallbackCameraAction callback){

        try {
            AppController.getInstance().turnLightOn = !AppController.getInstance().turnLightOn;
            Camera.Parameters params = getmCamera().getParameters();

            if (AppController.getInstance().turnLightOn) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                getmCamera().setParameters(params);
                getmCamera().startPreview();
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                getmCamera().setParameters(params);
                getmCamera().startPreview();
            }
            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    @Override
    public void enableQRCode(boolean enable) {

        Log.d("myapp", "*************************enableQRCodeScan: " + enable);
        if(enable) {
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            scanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);

            barcodeScanned = false;
            if (getActivity() != null)
                getmCamera().setPreviewCallback(previewCb);
        }else{
            if (getActivity() != null)
                getmCamera().setPreviewCallback(null);
            barcodeScanned = true;
            scanner = null;
        }

    }

    @Override
    public void enableFrameCapture(boolean enable) {
        captureFrame = enable;
        if (enable) {
            barcodeScanned = true;
            getmCamera().setPreviewCallback(previewCb);
        } else {
            getmCamera().setPreviewCallback(null);
        }
    }

    @Override
    public CameraFrame getCurrentFrame() {
        Log.d("gif", "getCurrentFrame:" + (frameData != null));
        return new CameraFrame(frameData, getmCamera().getParameters().getPreviewSize().width, getmCamera().getParameters().getPreviewSize().height);
    }

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback()
    {
        public void onPreviewFrame(byte[] data, Camera camera)
        {

            try {

                if(captureFrame){
                    frameData = data;
                }

                timeCurrent = System.currentTimeMillis();
                long elapsedTimeNs = timeCurrent - timeOld;
                if (elapsedTimeNs < 1000) {
                    return;
                }
                timeOld = timeCurrent;


                if(barcodeScanned)
                    return;
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);
                if (result != 0 && !barcodeScanned)
                {
                    barcodeScanned = true;
                    SymbolSet syms = scanner.getResults();
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




}