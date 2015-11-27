package com.tappitz.tappitz.rest.model;

import com.tappitz.tappitz.Global;

public class PhotoInbox {

    private int pictureId;
    private String pictureSentence;
    private String authorName;
    private String sentDate;
    private boolean hasVoted;
    private String votedDate;
    private String comment;
    private int vote;


    public PhotoInbox(int pictureId, String pictureSentence, String authorName, String sentDate, boolean hasVoted, String votedDate, String comment, int vote) {
        this.pictureId = pictureId;
        this.pictureSentence = pictureSentence;
        this.authorName = authorName;
        this.sentDate = sentDate;
        this.hasVoted = hasVoted;
        this.votedDate = votedDate;
        this.comment = comment;
        this.vote = vote;
    }

    public String getUrl() {
        return Global.ENDPOINT + "/pictures/"+pictureId;
///  return "https://upload.wikimedia.org/wikipedia/pt/e/ed/IST_Logo.png";
    }


    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureSentence() {
        return pictureSentence;
    }

    public void setPictureSentence(String pictureSentence) {
        this.pictureSentence = pictureSentence;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getVotedDate() {
        return votedDate;
    }

    public void setVotedDate(String votedDate) {
        this.votedDate = votedDate;
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
