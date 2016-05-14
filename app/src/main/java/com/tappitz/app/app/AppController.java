package com.tappitz.app.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tappitz.app.R;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.ErrorLogEntry;
import com.tappitz.app.rest.model.GoogleId;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.PrintWriter;
import java.io.StringWriter;


import retrofit2.Call;

//import com.squareup.okhttp.Request;

@ReportsCrashes(
        mailTo = "watermelonprojects@gmail.com",//"joaosampaio30@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogTitle = R.string.crash_dialog_title,
        resToastText = R.string.crash_toast_text)
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static Context context;

    public Camera mCamera;
    public SurfaceHolder surfaceHolder;
    public int currentCameraId = 1;
    public int width;
    public int height;
    private String sessionId;
    public boolean turnLightOn;

    public boolean mCameraReady;
    public String email;

    private static AppController mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

//        ACRA.init(this);
        mInstance = this;
        AppController.context = getApplicationContext();

        // Setup handler for uncaught exceptions.
//        final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable e) {
//                handleUncaughtException(thread, e);
//                oldHandler.uncaughtException(thread, e);
//            }
//        });
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;
        final int androidVersion = Build.VERSION.SDK_INT;
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        final String appVersion = (info == null ? "(null)" : info.versionCode).toString();

        final String errorLog = getStringFromStackTrace(e);
        final String phoneData = "sdk: " + androidVersion + "|model: " + model + "|version: " + appVersion;
        final ErrorLogEntry errorEntry = new ErrorLogEntry(errorLog, phoneData);


        Call<JsonElement> call = RestClientV2.getService().sendErrorLog(errorEntry);
        call.enqueue(new retrofit2.Callback<JsonElement>() {
             @Override
             public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {

             }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    private static String getStringFromStackTrace(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return AppController.context;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}