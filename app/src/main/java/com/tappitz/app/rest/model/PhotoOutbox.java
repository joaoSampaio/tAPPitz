package com.tappitz.app.rest.model;

import com.tappitz.app.Global;
import com.tappitz.app.util.DateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoOutbox {

    private int id;
    private String comment;
    private boolean isGif;
    private String createdDate;

    public PhotoOutbox( int id, String text){
        this.id = id;
        this.comment = text;
    }

    public String getUrl() {
        return Global.ENDPOINT + "/pictures/"+id;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return comment;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    public long getTimeMilliseconds(){
        long time = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;

            date = sdf.parse(createdDate);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public String getTimeAgo(){
        String tmp = DateHelper.getTimeAgo(getTimeMilliseconds());
        return tmp;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
