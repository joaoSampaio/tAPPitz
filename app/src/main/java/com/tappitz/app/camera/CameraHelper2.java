package com.tappitz.app.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.tappitz.app.model.CameraFrame;
import com.tappitz.app.model.FutureUpload;
import com.tappitz.app.model.SentPicture;
import com.tappitz.app.ui.BlankFragment;
import com.tappitz.app.ui.MainActivity;
import com.tappitz.app.ui.secondary.SelectContactFragment;

import java.util.ArrayList;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconEditText;


public class CameraHelper2 implements View.OnClickListener, View.OnLongClickListener {

    private final static int MAXFRAMES = 3;
    private final static int PERIOD_GIF = 3000;
    RelativeLayout layout_after_photo, layout_before_photo;
    MainActivity activity;
    CameraPreview4 previewView;
    private String photoPath;
    private byte[] photoData;
    private EmojiconEditText textMsg;
    private ImageView emoji_btn, temp_pic;
    private Button btn_flash;
    private ArrayList<Bitmap> bitmapsGif;
    private boolean isLongClickActive = false;
    private Handler handler;
    private Runnable gifRunnable, counterRunnable;
    private int numFrames = 0, countTilNext = MAXFRAMES;
    private View gif_box_container, edittext_next_container;
    private TextView textViewCount, textViewCountDescription;
    final static int[] CLICABLES = {R.id.camera_options, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btnPhotoDelete, R.id.btnPhotoAccept};
    private List<ImageView> gifSequence;
    private SaveGifThread saveGifThread;

    public CameraHelper2(MainActivity act, final CameraPreview4 preview4) {
        this.activity = act;
        this.previewView = preview4;
        layout_after_photo = (RelativeLayout)activity.findViewById(R.id.layout_after_photo);
        layout_before_photo = (RelativeLayout)activity.findViewById(R.id.layout_before_photo);
        layout_after_photo.setVisibility(View.GONE);
        layout_before_photo.setVisibility(View.VISIBLE);
        textMsg = (EmojiconEditText)activity.findViewById(R.id.textMsg);
        emoji_btn = (ImageView) activity.findViewById(R.id.emoji_btn);
        temp_pic = (ImageView) activity.findViewById(R.id.temp_pic);
        btn_flash = (Button) activity.findViewById(R.id.btn_flash);
        gif_box_container = activity.findViewById(R.id.gif_box_container);
        edittext_next_container = activity.findViewById(R.id.edittext_next_container);
        isLongClickActive = false;
        bitmapsGif = new ArrayList<>();
        gifSequence = new ArrayList<>();
        gifSequence.add((ImageView) getActivity().findViewById(R.id.gif_box1));
        gifSequence.add((ImageView) getActivity().findViewById(R.id.gif_box2));
        gifSequence.add((ImageView) getActivity().findViewById(R.id.gif_box3));
        textViewCount = (TextView) activity.findViewById(R.id.textViewCount);
        textViewCountDescription = (TextView) activity.findViewById(R.id.textViewCountDescription);
        handler = new Handler();
        gifRunnable = new Runnable() {
            @Override
            public void run() {
                if(isLongClickActive && numFrames < MAXFRAMES) {
                    Log.d("gif", "Runnable:"+numFrames);
                    if(previewView != null){
                        if(numFrames < (MAXFRAMES - 1))
                            handler.post(counterRunnable);
                        CameraFrame frame =  previewView.getCurrentFrame();
                        generateBitmapIfGif(frame.getBitmapData(), frame.getWidth(), frame.getHeight());
                    }
                    if (handler != null)
                        handler.postDelayed(this, 1 * PERIOD_GIF);
                }
            }
        };

        counterRunnable = new Runnable() {
            @Override
            public void run() {
                if(countTilNext == 0)
                    countTilNext = MAXFRAMES;
                textViewCount.setText(""+countTilNext);
                textViewCountDescription.setText("Next picture in "+ countTilNext +" seconds.");
                countTilNext--;
                if(countTilNext > 0)
                    handler.postDelayed(this, 1000);

            }
        };


        temp_pic.setVisibility(View.GONE);
        showBtnOptions(true);
        layout_after_photo.setVisibility(View.GONE);
        onTakePick(false);
        showGifContainer(false);


        for(int id: CLICABLES)
            getActivity().findViewById(id).setOnClickListener(this);

        layout_after_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        imm.hideSoftInputFromWindow(layout_after_photo.getWindowToken(), 0);
                        layout_after_photo.setVisibility(View.INVISIBLE);
                        return true;
                    case MotionEvent.ACTION_POINTER_UP:
                        layout_after_photo.setVisibility(View.VISIBLE);
                        layout_after_photo.bringToFront();
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onClick(View v) {
        View mView;
        switch (v.getId()){
            case R.id.camera_options:

                mView = getActivity().findViewById(R.id.layout_camera);
                mView.setVisibility(mView.isShown()? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_shutter:

                previewView.takePhoto(mPicture, new CallbackCameraAction() {
                    @Override
                    public void onSuccess() {
                        (getActivity()).enableSwipe(false);
                        onTakePick(true);
                        getActivity().screenHistory.add(0, 0);
                        edittext_next_container.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("myapp", "onFailure");
                    }
                });
                break;

            case R.id.btnPhotoDelete:

                deletePhoto();

                break;

            case R.id.btn_flash:
                enableCameraButtons(false);
                previewView.toggleFlash(new CallbackCameraAction() {
                    @Override
                    public void onSuccess() {

                        btn_flash.setTextColor(AppController.getInstance().turnLightOn ? Color.YELLOW : Color.WHITE);
                        enableCameraButtons(true);
                    }

                    @Override
                    public void onFailure() {
                        enableCameraButtons(true);
                    }
                });
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
            case R.id.btn_toggle_camera:
                enableCameraButtons(false);
                PackageManager pm = getActivity().getPackageManager();

                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT ) || Camera.getNumberOfCameras() <= 1) {
                    enableCameraButtons(true);
                    Toast.makeText(getActivity(), "You only have one camera!", Toast.LENGTH_SHORT).show();
                    break;
                }
                previewView.turnCamera(new CallbackCameraAction() {
                    @Override
                    public void onSuccess() {
                        enableCameraButtons(true);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("myapp", "btn_toggle_camera onError:");
                        enableCameraButtons(true);
                    }
                });

                break;
            case R.id.btn_load:

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, "Seleccionar ficheiro"), Global.BROWSE_REQUEST);

                break;
        }
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap

            photoData = data;

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
//                        loadBitmapFile(temp_pic, photoPath, AppController.getInstance().width, AppController.getInstance().height);
                        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        photoData = null;
                        Toast.makeText(activity, "done", Toast.LENGTH_LONG);
//                        btn_back.setEnabled(true);

                    }

                }
            }).execute();

        }
    };

    public void showBtnOptions(boolean show){
        getActivity().findViewById(R.id.camera_options).setVisibility(!show ? View.GONE : View.VISIBLE);
        getActivity().findViewById(R.id.layout_camera).setVisibility(View.GONE);
    }

    public void deletePhoto(){
        handler.removeCallbacksAndMessages(null);
        previewView.restartPreview();
        onTakePick(false);
        textMsg.setText("");
        photoPath = "";
        photoData = null;
        saveGifThread = null;
        bitmapsGif.clear();
        (getActivity()).enableSwipe(true);
        temp_pic.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(previewView.getWindowToken(), 0);
        if(!getActivity().screenHistory.isEmpty())
            getActivity().screenHistory.remove(0);
    }

    public void onTakePick(final boolean takePhoto)
    {
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

    }

    private void showDialog() {

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
                deletePhoto();

                //lançar o serviço
                AppController.getAppContext().startService(new Intent(AppController.getAppContext(), BackgroundService.class));
                newFragment.dismiss();
            }
        });

        ft.add(newFragment, "dialog");
        ft.commitAllowingStateLoss();

    }

    private void enableCameraButtons(boolean enable){

        BlankFragment.ButtonEnable listener = ( activity).getButtonEnable();
        if(listener != null){
            listener.enableCameraButtons(enable);
        }
    }

    public void enableQRCodeCapture(boolean enable){
        Log.d("myapp2", "**--qr code enabled:" + enable);

        if(previewView != null){
            previewView.enableQRCode(enable);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Global.BROWSE_REQUEST && resultCode == Activity.RESULT_OK&& null != data) {
            (getActivity()).enableSwipe(false);
            Uri selectedImageUri = data.getData();
            photoPath = UriPath.getPath(getActivity(), selectedImageUri);
            Glide.with(getActivity())
                    .load(photoPath)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .into(temp_pic);
            temp_pic.setVisibility(View.VISIBLE);
            onTakePick(true);
        }else {
            ( getActivity()).enableSwipe(true);
        }
    }

    public MainActivity getActivity() {
        return activity;
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.btn_shutter:
                if(handler != null && !isLongClickActive) {
                    bitmapsGif.clear();
                    numFrames = 0;
                    isLongClickActive = true;
                    edittext_next_container.setVisibility(View.GONE);
                    onTakePick(true);
                    getActivity().screenHistory.add(0, 0);
                    (activity).enableSwipe(false);
                    previewView.enableFrameCapture(true);
                    showGifContainer(true);
                    handler.post(counterRunnable);
                    handler.postDelayed(gifRunnable, 3000);
                }
            break;
        }

        return true;
    }

    private void showGifContainer(boolean show){
        gif_box_container.setVisibility(show? View.VISIBLE : View.GONE);
        int[] ids = {R.id.gif_box1, R.id.gif_box2, R.id.gif_box3};
        for(int i= 0;i < gifSequence.size(); i++){
            gifSequence.get(i).setImageResource(R.drawable.square_shape_gif_empty);
        }
    }

    private void changeColorGif(){

        Toast.makeText(getActivity(), "frame:"+numFrames, Toast.LENGTH_SHORT).show();
        if(gifSequence.size() > numFrames ) {
            gifSequence.get(numFrames).setImageResource(R.drawable.square_shape_gif_full);
        }
    }

    private void generateBitmapIfGif(byte[] bitmapdata, int width, int height){
        Bitmap originalBitmap = PhotoSave.getBitmapImageFromYUV(bitmapdata, width, height );

        changeColorGif();

        int orientation = Exif.getOrientation(bitmapdata);

        if(width > height) {
            if(AppController.getInstance().currentCameraId == 0)
                orientation = 90;
            else
                orientation = 270;
        }
        switch(orientation) {
            case 90:
                originalBitmap = PhotoSave.RotateBitmap(originalBitmap, 90);
                break;
            case 270:
                originalBitmap = PhotoSave.RotateBitmap(originalBitmap, 270);
                break;

        }


        bitmapsGif.add(getResizedBitmap(originalBitmap, originalBitmap.getHeight()/2, originalBitmap.getWidth()/2));

        if(saveGifThread == null) {
            saveGifThread = new SaveGifThread(bitmapsGif, listenerGif);
            Thread waiterThread = new Thread(saveGifThread, "waiterThread");
            waiterThread.start();
        }

        synchronized (bitmapsGif) {
            bitmapsGif.notify();
        }

        if(numFrames == (MAXFRAMES -1)) {
            previewView.enableFrameCapture(false);
            saveGifToFile();
        }
        numFrames++;

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    long time1, time2;
    public void saveGifToFile(){
        Log.d("gif", "save gif");
        isLongClickActive = false;
        time1 = System.currentTimeMillis();
        //new SaveGifBackgroundTask(bitmapsGif, listenerGif).execute();
    }


    SaveGifThread.GifSaved listenerGif = new SaveGifThread.GifSaved() {
        @Override
        public void onGifSaved(Uri uri, String photoPath) {
            time2 = System.currentTimeMillis();
            Log.d("gif", "onGifSaved took:" + ((time2 - time1)/1000) + "seconds to save to disk");

            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Toast.makeText(AppController.getAppContext(), "Gif saved", Toast.LENGTH_SHORT);

            saveGifThread = null;

            if (temp_pic != null) {
                temp_pic.setVisibility(View.VISIBLE);
            }
            showGifContainer(false);
            Glide.with(getActivity())
                    .load(photoPath)
                    .asGif()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.IMMEDIATE)
                    .listener(new RequestListener<String, GifDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                            if(e != null)
                                Log.d("glide", "exception->"+e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(temp_pic);
            edittext_next_container.setVisibility(View.VISIBLE);

        }
    };


}
