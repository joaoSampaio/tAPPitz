package com.tappitz.app.model;

/**
 * Created by Sampaio on 02/06/2016.
 */
public class CameraFrame {

    byte[] bitmapData;
    int width,  height;

    public CameraFrame(byte[] bitmapData, int width, int height) {
        this.bitmapData = bitmapData;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public byte[] getBitmapData() {
        return bitmapData;
    }

    public int getHeight() {
        return height;
    }
}
