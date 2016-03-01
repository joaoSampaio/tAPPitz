package com.tappitz.tappitz.model;

import java.util.List;

/**
 * Created by joaosampaio on 24-02-2016.
 */
public class FutureVote {

    private int pictureId;
    private String comment;
    private int vote;

    public FutureVote(int pictureId, String comment, int vote) {
        this.pictureId = pictureId;
        this.comment = comment;
        this.vote = vote;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
