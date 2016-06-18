package com.tappitz.app.model;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.app.AppController;
import com.tappitz.app.util.DateHelper;
import com.tappitz.app.util.ModelCache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ReceivedPhoto {

    private int pictureId;
    private String pictureSentence;
    private String authorName;
    private String sentDate;
    private boolean hasVoted;
    private String votedDate;
    private String comment;
    private int vote;
    private boolean isVoteTemporary;
    private boolean isGif;


    public ReceivedPhoto(int pictureId, String pictureSentence, String authorName, String sentDate, boolean hasVoted, String votedDate, String comment, int vote) {
        this.pictureId = pictureId;
        this.pictureSentence = pictureSentence;
        this.authorName = authorName;
        this.sentDate = sentDate;
        this.hasVoted = hasVoted;
        this.votedDate = votedDate;
        this.comment = comment;
        this.vote = vote;
    }

    public ReceivedPhoto(int pictureId, String comment, int vote) {
        this.pictureId = pictureId;
        this.comment = comment;
        this.vote = vote;
    }

    public ReceivedPhoto(int pictureId, String pictureSentence, String authorName) {
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

    public static long getTimeMilliseconds(String dateTime){
        long time = 0;
        Log.d("myapp", "**--Received getTimeMilliseconds  ");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;
            date = sdf.parse(dateTime);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("myapp", "**--Received getTimeMilliseconds  " + time);
        return time;
    }

    public static String getTimeAgo(String dateTime){
        String tmp =DateHelper.getTimeAgo(getTimeMilliseconds(dateTime));
        Log.d("myapp", "**--Received getTimeAgo  " + tmp);
        return tmp;
    }

    public boolean isVoteTemporary() {
        return isVoteTemporary;
    }

    public void setIsVoteTemporary(boolean isTemporary) {
        this.isVoteTemporary = isTemporary;
    }


    public static ReceivedPhoto getPhotoWithId(List<ReceivedPhoto> received, int id){

        List<ReceivedPhoto> copy = new ArrayList<>(received);
        for(Iterator<ReceivedPhoto> it = copy.iterator(); it.hasNext();) {
            ReceivedPhoto rec = it.next();
            if(rec.getPictureId() == id)
                return rec;
        }
        return null;
    }

    public static boolean hasId(List<ReceivedPhoto> received, int id){
        ReceivedPhoto r = getPhotoWithId(received, id);
        return r != null;
    }

    //mantem inbox com votos finais, remove temporarios caso nao haja work relacionado com eles, se
    // houver um inbox por votar e existir um work com esse id manter o nosso temporario
    public static List<ReceivedPhoto> join(List<ReceivedPhoto> serverPictures){
        FutureWorkList work = new ModelCache<FutureWorkList>().loadModel(AppController.getAppContext(), new TypeToken<FutureWorkList>() {
        }.getType(), Global.OFFLINE_WORK);

        if(work != null) {
            List<FutureVote> votes = work.getVotes();

            ReceivedPhoto result;
            for (FutureVote future : votes) {
                result = getPhotoWithId( serverPictures, future.getPictureId());
                if(result != null){
                    result.setIsVoteTemporary(true);
                    result.setComment(future.getComment());
                    result.setVote(future.getVote());
                }
            }
        }
        return serverPictures;
    }

    public static List<ImageModel> generateImageGallery(List<ReceivedPhoto> receivedPhotos){

        List<ImageModel> images = new ArrayList<>();
        ImageModel img;
        for (ReceivedPhoto photo: receivedPhotos) {
            img = new ImageModel(photo.getUrl(), ImageModel.TYPE_INBOX, photo.getPictureId());
            img.setVote(photo.getVote());
            img.setHasVoted(photo.isHasVoted());
            images.add(img);
        }
        return  images;
    }

}
