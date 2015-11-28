package com.tappitz.tappitz.rest.model;

import com.tappitz.tappitz.model.Comment;

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
            return new Comment(getVote(), getComment(), getVotedDate(), getReceiverName());
        }else {
            return null;
        }
    }
}
