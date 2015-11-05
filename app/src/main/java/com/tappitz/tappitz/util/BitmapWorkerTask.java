package com.tappitz.tappitz.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String path;
    private int height, width;

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
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    original = RotateBitmap(original, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    original = RotateBitmap(original, 180);
                    break;
                // etc.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        return original;
    }




    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
    }
}