package com.tappitz.tappitz.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<ImageModel> data = new ArrayList<>();

    public GalleryAdapter(Context context, List<ImageModel> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.gallery_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(data.get(position).getTYPE() == ImageModel.TYPE_INBOX) {
            int vote = data.get(position).getVote();
            boolean hasVoted = data.get(position).isHasVoted();
            if(hasVoted) {
                ((MyItemHolder) holder).background.setVisibility(View.VISIBLE);
                ((MyItemHolder) holder).background.setBackgroundColor(context.getResources().getColor(getColor(vote)));
            }else{
                ((MyItemHolder) holder).background.setVisibility(View.GONE);
            }
        }else{
            ((MyItemHolder) holder).background.setVisibility(View.GONE);
        }
        Glide.with(context).load(data.get(position).getUrl())
                .thumbnail(0.5f)
                .override(200,200)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((MyItemHolder) holder).mImg);

    }

    private int getColor(int order){
        int color = 0;
        switch (order){
            case Global.RED:
                color =  R.color.redA;
                break;
            case Global.YELLOW:
                color = R.color.yellowA;
                break;
            case Global.GREEN:
                color = R.color.greenA;
                break;
        }
        return color;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        View background;

        public MyItemHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            mImg = (ImageView) itemView.findViewById(R.id.item_img);
        }

    }
}
