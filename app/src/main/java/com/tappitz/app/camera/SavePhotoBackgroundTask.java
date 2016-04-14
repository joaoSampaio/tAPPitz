package com.tappitz.app.camera;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;

public class SavePhotoBackgroundTask extends AsyncTask<Void, Void, String> {

    private SaveNewRotatedPictureInterface listener;
    private Uri uri;
    private String photoPath;
    private byte[] photoData;

    public SavePhotoBackgroundTask(byte[] photoData, SaveNewRotatedPictureInterface listener) {
        this.photoData = photoData;
        this.listener = listener;

    }

    // Decode image in background.
    @Override
    protected String doInBackground(Void... params) {

        try {
            File file = PhotoSave.saveImageToFile(photoData);
            photoPath = file.getAbsolutePath();
            uri=  Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }






    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(String something) {

        if(listener != null){
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