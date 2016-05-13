package com.tappitz.app.rest.model;

import java.util.List;

/**
 * Created by sampaio on 25-10-2015.
 */
public class CreatePhoto {

    private boolean sendToFollowers;
    private String comment;
    private String picture;
    private List<Integer> friendIds;
    private boolean isGif;

    public CreatePhoto(String comment, List<Integer> contacts, String picture, boolean sendToFollowers) {
        this.comment = comment;
        this.friendIds = contacts;
        this.picture = picture;
        this.sendToFollowers = sendToFollowers;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Integer> getContacts() {
        return friendIds;
    }

    public void setContacts(List<Integer> contacts) {
        this.friendIds = contacts;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<Integer> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(List<Integer> friendIds) {
        this.friendIds = friendIds;
    }

    public boolean isSendToFollowers() {
        return sendToFollowers;
    }

    public void setSendToFollowers(boolean sendToFollowers) {
        this.sendToFollowers = sendToFollowers;
    }
}
