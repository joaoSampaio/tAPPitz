package com.tappitz.app.camera;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tappitz.app.util.AnimatedGifEncoder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sampaio on 03/06/2016.
 */
public class SaveGifThread implements Runnable {

    ArrayList<Bitmap> bitmapsSynchronized;
    private int count = 0;
    private GifSaved listenerThread;
    private Uri uri;
    private String photoPath;

    public SaveGifThread(ArrayList<Bitmap> bitmapsSynchronized, GifSaved listenerT) {
        this.bitmapsSynchronized = bitmapsSynchronized;
        this.listenerThread = listenerT;
    }

    @Override
    public void run() {
        Log.d("gif", "SaveGifThread: " + new Date());
        synchronized (bitmapsSynchronized) {
            try {
                BufferedOutputStream bos = null;
                try {
                    File file = PhotoSave.getOutputMediaFile("gif");
                    uri = Uri.fromFile(file);
                    photoPath = file.getAbsolutePath();


                    FileOutputStream outStream = null;
                    outStream = new FileOutputStream(file);
                    bos = new BufferedOutputStream(outStream);

                    AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                    encoder.setDelay(300);
                    encoder.setQuality(20);
                    encoder.start(bos);


                    encoder.addFrame(bitmapsSynchronized.get(count));
                    count++;
                    Log.d("gif", "Waiter is waiting for the notifier at " + new Date());
                    bitmapsSynchronized.wait();
                    encoder.addFrame(bitmapsSynchronized.get(count));
                    count++;
                    Log.d("gif", "Waiter is waiting for the notifier at " + new Date());
                    bitmapsSynchronized.wait();
                    encoder.addFrame(bitmapsSynchronized.get(count));
                    count++;

                    encoder.finish();
                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            Log.d("gif", "save gif doInBackground");
                            listenerThread.onGifSaved(uri, photoPath);
                        }
                    });
                    bitmapsSynchronized.clear();
                    bitmapsSynchronized = null;
                } catch (FileNotFoundException fnfe) {
                    System.out.println("File not found" + fnfe);
                } catch (IOException ioe) {
                    System.out.println("Error while writing to file" + ioe);
                } finally {
                    try {
                        if (bos != null) {
                            bos.flush();
                            bos.close();
                        }
                    } catch (Exception e) {
                        System.out.println("Error while closing streams" + e);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){

    }


    public interface GifSaved{
        public void onGifSaved(Uri uri, String photoPath);
    }

    }
