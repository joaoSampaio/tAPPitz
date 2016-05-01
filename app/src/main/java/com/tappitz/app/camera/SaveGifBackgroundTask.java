package com.tappitz.app.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.tappitz.app.util.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SaveGifBackgroundTask extends AsyncTask<Void, Void, String> {

    private Uri uri;
    private ArrayList<Bitmap> bitmaps;
    private String photoPath;
    private GifSaved listener;
    public SaveGifBackgroundTask(ArrayList<Bitmap> bitmaps, GifSaved listener) {
        this.bitmaps = bitmaps;
        this.listener = listener;
    }

    // Decode image in background.
    @Override
    protected String doInBackground(Void... params) {

        Log.d("gif", "save gif doInBackground");
        FileOutputStream outStream = null;
        try{
            File file = PhotoSave.getOutputMediaFile("gif");
            uri = Uri.fromFile(file);
            photoPath = file.getAbsolutePath();
            outStream = new FileOutputStream(file);
            outStream.write(generateGIF());
            outStream.close();

            file = null;
        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }






    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(String something) {
        Log.d("gif", "onPostExecute");
        if(listener != null){
            listener.onGifSaved(uri, photoPath);
        }
    }



    public interface GifSaved{
        public void onGifSaved(Uri uri, String photoPath);
    }

    public byte[] generateGIF() {
//        ArrayList<Bitmap> bitmaps = adapter.getBitmapArray();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(1000);
        encoder.setQuality(20);
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d("gif", "bitmap:"+bitmap.getAllocationByteCount());
            }
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }


}