package com.tappitz.app.camera;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoSave {

    @SuppressLint("SimpleDateFormat") public static File getOutputMediaFile()
    {
        return getOutputMediaFile("jpg");
    }

    @SuppressLint("SimpleDateFormat") public static File getOutputMediaFile(String extension){
        if (true == isExternalStoragePresent())
        {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString() + "/tAPPitz");

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
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "tAPPitz_" + timeStamp + "."+extension);
            return mediaFile;
        }
        else
        {
            try
            {
                //new AlertDialog.Builder(getActivity()).setMessage("No storage space found, can't save the video.").setPositiveButton("Ok", null).show();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }


    public static boolean isExternalStoragePresent()
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
            //showToast("SD card not present");
            //Toast.makeText(getActivity(), "SD card not present", Toast.LENGTH_LONG);
        }

        return (mExternalStorageAvailable) && (mExternalStorageWriteable);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public static File saveImageToFile(byte[] photoData){
        File file = null;
        if(photoData != null) {
            //FileOutputStream outStream = null;
            try {
                int orientation = Exif.getOrientation(photoData);
                Log.d("foto", "Save to File orientation: " + orientation);
                Bitmap originalBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length, null);


                switch(orientation) {
                    case 90:
                        originalBitmap = PhotoSave.RotateBitmap(originalBitmap, 90);
                        break;
                    case 270:
                        originalBitmap = PhotoSave.RotateBitmap(originalBitmap, 270);
                        break;

                }


                    FileOutputStream outStream = null;
                    try {
                        file = PhotoSave.getOutputMediaFile();
                        outStream = new FileOutputStream(file);
                        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream); // bmp is your Bitmap instance
                        outStream.close();

//                        photoPath = file.getAbsolutePath();
//                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                    } catch (FileNotFoundException e) {
                        Log.d("CAMERA", e.getMessage());
                    } catch (IOException e) {
                        Log.d("CAMERA", e.getMessage());
                    }

            } catch (Exception e) {
                Log.d("CAMERA", e.getMessage());
            }
            photoData = null;
        }
        return file;
    }

    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }

}
