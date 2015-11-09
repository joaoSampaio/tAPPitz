package com.tappitz.tappitz.camera;


import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoSave {

    @SuppressLint("SimpleDateFormat") public static File getOutputMediaFile()
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


            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");



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

}
