package com.tappitz.tappitz.rest.model;

import java.util.List;

/**
 * Created by sampaio on 25-10-2015.
 */
public class CreatePhoto {

    private String comment;
    private String picture;
    private List<String> contacts;

    public CreatePhoto(String comment, List<String> contacts, String picture) {
        this.comment = comment;
        this.contacts = contacts;
        this.picture = picture;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
