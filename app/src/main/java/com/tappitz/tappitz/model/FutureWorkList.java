package com.tappitz.tappitz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaosampaio on 24-02-2016.
 */
public class FutureWorkList {

    private List<FutureUpload> uploads;
    private List<FutureVote> votes;

    public FutureWorkList() {
        uploads = new ArrayList<>();
        votes = new ArrayList<>();
    }

    public boolean hasWork(){
        return uploads.size() > 0 || votes.size() > 0;
    }

    public void addUploadWork(FutureUpload upload){
        uploads.add(upload);
    }

    public void addVoteWork(FutureVote vote){
        votes.add(vote);
    }


    public void removeUpload(int tmpId){
        FutureUpload toRemove = null;
        for (FutureUpload upload: getUploads()) {
            if(upload.getTmpId() == tmpId){
                toRemove = upload;
                break;
            }
        }
        if(toRemove != null)
            getUploads().remove(toRemove);
    }

    public void removeVote(int pictureId){
        FutureVote toRemove = null;
        for (FutureVote vote: getVotes()) {
            if(vote.getPictureId() == pictureId){
                toRemove = vote;
                break;
            }
        }
        if(toRemove != null)
            getVotes().remove(toRemove);
    }


    public List<FutureUpload> getUploads() {
        return uploads;
    }

    public void setUploads(List<FutureUpload> uploads) {
        this.uploads = uploads;
    }

    public List<FutureVote> getVotes() {
        return votes;
    }

    public void setVotes(List<FutureVote> votes) {
        this.votes = votes;
    }

    public boolean uploadHasTempId(int tempId){
        for (FutureUpload upload: this.getUploads()) {
            if(upload.getTmpId() == tempId)
                return true;
        }
        return false;
    }

    public List<SentPicture> removeFailed(List<SentPicture> fullList){
        List<SentPicture> toBeRemoved = new ArrayList<>();
        for (SentPicture pic:fullList) {
            if(pic.isTemporary()){
                //esta foto é temporaria
                if(!uploadHasTempId(pic.getId())){
                    //esta foto tem de ser apagada, nao existe work relacionado com ela.
                    toBeRemoved.add(pic);
                }
            }
        }
        if(toBeRemoved.size() > 0)
            fullList.removeAll(toBeRemoved);

        return fullList;
    }

}
