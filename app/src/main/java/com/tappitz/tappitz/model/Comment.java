package com.tappitz.tappitz.model;

/**
 * Created by sampaio on 06-10-2015.
 */
public class Comment {

    //indica se é vermelho, amarelo ou verde. 0,1 e 2 respectivamente
    private int rate;
    private String comment;
    private String dateSent;
    private String name;

    public Comment(int rate, String comment, String dateSent, String name) {
        this.rate = rate;
        this.comment = comment;
        this.dateSent = dateSent;
        this.name = name;
    }

    public Comment(int rate, String name, String dateSent) {
        this.rate = rate;
        this.name = name;
        this.dateSent = dateSent;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
