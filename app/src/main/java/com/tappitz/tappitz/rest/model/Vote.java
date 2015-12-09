package com.tappitz.tappitz.rest.model;

import com.tappitz.tappitz.model.Comment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sampaio on 27-11-2015.
 */
public class Vote {
    private boolean hasVoted;
    private String receiverName;
    private String sentDate;
    private String votedDate;
    private int vote;
    private String comment;

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getVotedDate() {
        return votedDate;
    }

    public void setVotedDate(String votedDate) {
        this.votedDate = votedDate;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Comment convertToComment(){
        if(hasVoted()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S",
                    Locale.ENGLISH);
            Date date = null;
            try {
                date = sdf.parse(votedDate);

                DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                votedDate = dfmt.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Comment(getVote(), getComment(), getVotedDate(), getReceiverName());
        }else {
            return null;
        }
    }
}
