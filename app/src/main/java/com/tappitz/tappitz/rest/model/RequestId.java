package com.tappitz.tappitz.rest.model;


public class RequestId {
    private String pictureId;

    public RequestId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getId() {
        return pictureId;
    }

    public void setId(String pictureId) {
        this.pictureId = pictureId;
    }
}
