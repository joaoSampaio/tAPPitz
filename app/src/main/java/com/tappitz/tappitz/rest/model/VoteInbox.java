package com.tappitz.tappitz.rest.model;



public class VoteInbox {

    private int id;
    private String myComment;
    private int choice;

    public VoteInbox(int choice, int id, String myComment) {
        this.choice = choice;
        this.id = id;
        this.myComment = myComment;
    }

    public int getChoice() {
        return choice;
    }

    public int getId() {
        return id;
    }

    public String getMyComment() {
        return myComment;
    }
}
