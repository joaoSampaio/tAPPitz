package com.tappitz.tappitz.model;


public class Photo {
    private String comment, id;

    public Photo(String comment, String id) {
        this.comment = comment;
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
