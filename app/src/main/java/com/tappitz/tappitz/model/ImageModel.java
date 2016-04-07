package com.tappitz.tappitz.model;


public class ImageModel {

    public static final int TYPE_INBOX  = 0;
    public static final int TYPE_OUTBOX  = 1;
    public static final int TYPE_OUTBOX_NOTIFICATION  = 2;
    public static final int TYPE_INBOX_NOTIFICATION  = 3;
    private String url;
    private int TYPE, id, vote;
    private boolean hasVoted;

    public ImageModel(String url, int TYPE, int id) {
        this.url = url;
        this.TYPE = TYPE;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
}
