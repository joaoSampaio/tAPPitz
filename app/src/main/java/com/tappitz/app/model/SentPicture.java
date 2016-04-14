package com.tappitz.app.model;

import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.app.AppController;
import com.tappitz.app.util.DateHelper;
import com.tappitz.app.util.ModelCache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SentPicture {

    private boolean isTemporary;
    private String pathPictureTemporary;

    private int id;
    private String comment;
    private String createdDate;

    public SentPicture(int id, String comment, String createdDate, boolean isTemporary){
        this.id = id;
        this.comment = comment;
        this.createdDate = createdDate;
        this.isTemporary = isTemporary;
    }

    public SentPicture( int id, String comment, boolean isTemporary) {
        this.isTemporary = isTemporary;
        this.id = id;
        this.comment = comment;
    }

    public SentPicture(String comment, String pathPictureTemporary) {
        Random rand = new Random();

        this.id = rand.nextInt();
        this.comment = comment;
        this.pathPictureTemporary = pathPictureTemporary;
        this.isTemporary = true;
    }

    public String getUrl() {
        return Global.ENDPOINT + "/pictures/"+id;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return comment;
    }


    private long getTimeMilliseconds(){
        long time = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;

            date = sdf.parse(createdDate);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public String getTimeAgo(){
        if(createdDate == null || createdDate.length() < 5){
            return "";
        }else{
            String tmp = DateHelper.getTimeAgo(getTimeMilliseconds());
            return tmp;
        }

    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }

    public String getPathPictureTemporary() {
        return pathPictureTemporary;
    }

    public void setPathPictureTemporary(String pathPictureTemporary) {
        this.pathPictureTemporary = pathPictureTemporary;
    }

    public void setId(int id) {
        this.id = id;
    }

    private static SentPicture getPicFromList(List<SentPicture> pictures, int id){
        for (SentPicture pic: pictures) {
            if(id == pic.getId())
                return pic;
        }
        return null;
    }


    public boolean belongsTo(List<SentPicture> pictures){
        return getPicFromList(pictures, getId()) != null;
    }

    public FutureUpload generateFutureWork(List<Integer> friendIds, boolean sendToFollowers){
        return new FutureUpload(this.getId(), this.getPathPictureTemporary(), friendIds, this.getText(), sendToFollowers);
    }

    //este metodo adiciona às fotos vindas do servidor aquelas que ainda estao temporarias e nao foram enviadas
    public static List<SentPicture> join(List<SentPicture> serverPictures, List<SentPicture> offlinePictures){
        FutureWorkList work = new ModelCache<FutureWorkList>().loadModel(AppController.getAppContext(), new TypeToken<FutureWorkList>() {
        }.getType(), Global.OFFLINE_WORK);

        if(work != null) {
            List<FutureUpload> uploads = work.getUploads();
            SentPicture result;
            for (FutureUpload future : uploads) {
                result = getPicFromList(offlinePictures, future.getTmpId());
                if(result != null){
                    serverPictures.add(0,result);
                }
            }
        }
        return serverPictures;
    }

    public static void removeId(List<SentPicture> serverPictures, int id){
        SentPicture old = null;
        for (SentPicture sent: serverPictures){
            if(sent.getId() == id){
                old = sent;
                break;
            }
        }
        if(old != null)
            serverPictures.remove(old);
    }

    public static List<ImageModel> generateImageGallery(List<SentPicture> sentPhotos){

        List<ImageModel> images = new ArrayList<>();
        for (SentPicture photo: sentPhotos) {
            images.add(new ImageModel(photo.getUrl(), ImageModel.TYPE_INBOX, photo.getId()));
        }
        return  images;
    }

    public static List<ImageModel> generateUnseenImageGallery(List<SentPicture> sentPhotos, Map<Integer, Integer> unseen){

        List<ImageModel> images = new ArrayList<>();
        for (SentPicture photo: sentPhotos) {
            if(unseen.containsKey(photo.getId()))
                images.add(new ImageModel(photo.getUrl(), ImageModel.TYPE_INBOX, photo.getId()));
        }
        return  images;
    }

}
