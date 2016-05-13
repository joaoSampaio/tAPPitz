package com.tappitz.app.rest.model;

import com.tappitz.app.Global;
import com.tappitz.app.util.DateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoInbox {

    private int pictureId;
    private String pictureSentence;
    private String authorName;
    private String sentDate;
    private boolean hasVoted;
    private String votedDate;
    private String comment;
    private boolean isGif;
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

    public PhotoInbox(int pictureId, String comment, int vote) {
        this.pictureId = pictureId;
        this.comment = comment;
        this.vote = vote;
    }

    public PhotoInbox(int pictureId, String pictureSentence, String authorName) {
        this.pictureId = pictureId;
        this.pictureSentence = pictureSentence;
        this.authorName = authorName;
        this.hasVoted = false;
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

    public long getTimeMilliseconds(String dateTime){
        long time = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;
            date = sdf.parse(dateTime);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public String getTimeAgo(String dateTime){
        String tmp =DateHelper.getTimeAgo(getTimeMilliseconds(dateTime));
        return tmp;
    }
}
