package com.tappitz.tappitz.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String path;
    private int height, width, rotation;
    private boolean isRotated ;
    private SaveNewRotatedPictureInterface listener;
    private Uri uri;
    private String photoPath;

    public BitmapWorkerTask(ImageView imageView, String path, int width, int height) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.path = path;
        this.width = width;
        this.height = height;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //return BitmapFactory.decodeFile(path, options);
        Log.d("myapp","width:"+ width + " height:" + height);

        Bitmap original = LoadBitmap.decodeSampledBitmapFromFile(path, width, height);
        ExifInterface ei = null;
        isRotated = false;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int heightBit = original.getHeight();
            int widthBit = original.getWidth();

            Log.d("foto", "orientation: " + orientation);

//            if(widthBit > heightBit)
//                orientation = 90;

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    original = PhotoSave.RotateBitmap(original, 90);
                    rotation = 90;
                    isRotated = true;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    original = PhotoSave.RotateBitmap(original, 180);
                    rotation = 180;
                    isRotated = true;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    original = PhotoSave.RotateBitmap(original, 270);
                    rotation = 270;
                    isRotated = true;
                    break;
                // etc.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(isRotated){

            FileOutputStream outStream = null;
            try {
                File file = PhotoSave.getOutputMediaFile();
                outStream = new FileOutputStream(file);
                original.compress(Bitmap.CompressFormat.JPEG, 90, outStream); // bmp is your Bitmap instance
                outStream.close();

                photoPath = file.getAbsolutePath();
                uri = Uri.fromFile(file);
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            } catch (FileNotFoundException e) {
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e) {
                Log.d("CAMERA", e.getMessage());
            }


        }


        return original;
    }






    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }
        }
        if(listener != null && isRotated){
            listener.onSaveToFileRotated(uri, photoPath);
        }
    }


    public void setListener(SaveNewRotatedPictureInterface listener) {
        this.listener = listener;
    }

    public interface SaveNewRotatedPictureInterface{
        public void onSaveToFileRotated(Uri uri, String photoPath);
    }




}