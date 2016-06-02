package com.tappitz.app.model;

import android.content.Intent;

/**
 * Created by Sampaio on 01/06/2016.
 */
public class ActivityResult {
    private int requestCode, resultCode;
    private Intent data;

    public ActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Intent getData() {
        return data;
    }
}
