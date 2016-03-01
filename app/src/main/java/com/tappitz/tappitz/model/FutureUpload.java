package com.tappitz.tappitz.model;

import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by joaosampaio on 24-02-2016.
 */
public class FutureUpload {

    private int tmpId;
    private String path;
    private List<Integer> friendIds;
    private String comment;

    public FutureUpload(int tmpId, String path, List<Integer> friendIds, String comment) {
        this.tmpId = tmpId;
        this.path = path;
        this.friendIds = friendIds;
        this.comment = comment;
    }

    public int getTmpId() {
        return tmpId;
    }

    public void setTmpId(int tmpId) {
        this.tmpId = tmpId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Integer> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(List<Integer> friendIds) {
        this.friendIds = friendIds;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBase64Image(){
        String pictureBase64 = null;
        try {
            InputStream inputStream = null;//You can get an inputStream using any IO API
            inputStream = new FileInputStream(path);
            byte[] buffer = new byte[8192];
            int bytesRead;

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output64.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            output64.close();

            pictureBase64 = output.toString();
            pictureBase64 = pictureBase64.replace("\n","");
            Log.d("myapp", "pictureBase64:" + pictureBase64);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pictureBase64;
    }

}
