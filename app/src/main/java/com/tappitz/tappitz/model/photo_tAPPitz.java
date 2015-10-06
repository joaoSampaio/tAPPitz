package com.tappitz.tappitz.model;

public class photo_tAPPitz {

    private String url;
    private String id;
    private String text;

    public photo_tAPPitz(String url, String id, String text){
        this.url = url;
        this.id = id;
        this.text = text;
    }





    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
