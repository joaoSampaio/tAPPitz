package com.tappitz.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tappitz.app.R;

import java.util.ArrayList;
import java.util.List;

public class OutBoxCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<String> data = new ArrayList<>();

    public OutBoxCommentAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_outbox_comment, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ((MyItemHolder) holder).text.setText(data.get(position));
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        TextView text;

        public MyItemHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.photo_comment);
        }

    }
}
