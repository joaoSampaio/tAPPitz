package com.tappitz.tappitz.model;

import com.tappitz.tappitz.util.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public long getTimeMilliseconds(){
        long time = 0;
        try {
            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = null;

            date = dfmt.parse(getDateSent());
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public String getTimeAgo(){
        return DateHelper.getTimeAgo(getTimeMilliseconds());
    }

    public static boolean alreadyExistsAuthor(List<Comment> comments, String authorName){
        for (Comment c: comments) {
            if(c.getName().equals(authorName))
                return true;
        }
        return false;
    }
}
