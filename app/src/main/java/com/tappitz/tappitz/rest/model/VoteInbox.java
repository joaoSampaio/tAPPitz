package com.tappitz.tappitz.rest.model;



public class VoteInbox {

    private int pictureId;
    private String comment;
    private int vote;

    public VoteInbox(int choice, int id, String myComment) {
        this.vote = choice;
        this.pictureId = id;
        this.comment = myComment;
    }

    public int getChoice() {
        return vote;
    }

    public int getId() {
        return pictureId;
    }

    public String getMyComment() {
        return comment;
    }
}
