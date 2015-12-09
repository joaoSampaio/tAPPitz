package com.tappitz.tappitz.ui.secondary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.UrlLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.model.ListViewContactItem;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.rest.model.PhotoOutbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListVotesService;
import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.ui.OutBoxFragment;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class OutBoxPageFragment extends Fragment implements View.OnClickListener {

    private View rootView, comment_layout;
    private TextView commentText, comment_user, descriptionText;
    private List<Comment> listGreen, listRed, listYellow;
    private int selectdList, selectedPos, id;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private LinearLayout buttonsContainer;
    private ListenerPagerStateChange state;
    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde};

    public static OutBoxPageFragment newInstance(Bundle args) {
        OutBoxPageFragment fragment = new OutBoxPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_outbox_child, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.picture);

        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        String text = getArguments().getString(Global.TEXT_RESOURCE);
        id = getArguments().getInt(Global.ID_RESOURCE);

        for(int idBtn: CLICKABLE) {
            rootView.findViewById(idBtn).setOnClickListener(this);
        }
        botaoVerde = (Button)rootView.findViewById(R.id.botaoVerde);
        botaoAmarelo = (Button)rootView.findViewById(R.id.botaoAmarelo);
        botaoVermelho = (Button)rootView.findViewById(R.id.botaoVermelho);
        color_background = (ImageView)rootView.findViewById(R.id.color_background);

        comment_layout = rootView.findViewById(R.id.comment_layout);
        listGreen = new ArrayList<>();
        listRed = new ArrayList<>();
        listYellow = new ArrayList<>();
        selectdList = -1;
        selectedPos = -1;

        buttonsContainer = (LinearLayout)rootView.findViewById(R.id.painelvotacao);
        descriptionText = (TextView) rootView.findViewById(R.id.photo_description);
        descriptionText.setText(text);

        commentText = (TextView) rootView.findViewById(R.id.photo_comment);
        comment_user = (TextView) rootView.findViewById(R.id.comment_user);

        loadVotesOffline();
        Log.d("ListVotesService", "getContext() != null" + (getContext() != null));
        if(((ScreenSlidePagerActivity)getActivity()).getOutbox_id() >= 0){

            Comment c = ((ScreenSlidePagerActivity) getActivity()).getCommentVote();
            if(c != null) {
                openComment(c.getName(), c.getDateSent(), c.getRate());
                ((ScreenSlidePagerActivity)getActivity()).setOutbox_id(-1);
                ((ScreenSlidePagerActivity)getActivity()).setCommentVote(null);
            }
        }else{
            new ListVotesService(id, new CallbackMultiple<List<Comment>, String>() {
                @Override
                public void success(List<Comment> comments) {
                    Log.d("ListVotesService", "getContext() != null" + (getContext() != null));
                    Log.d("ListVotesService", "app getApplicationContext() != null" + (AppController.getInstance().getApplicationContext() != null));
                    Context ctx = AppController.getInstance().getApplicationContext();
                    new ModelCache<List<Comment>>().saveModel(ctx, comments, Global.OFFLINE_VOTE + id);
                    if(getActivity()!= null) {
                        sortVotes(comments);
                    }

                }

                @Override
                public void failed(String error) {

                }
            }).execute();
        }

        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        rootView.findViewById(R.id.picture).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        Log.d("myapp", "inbox ACTION_DOWN");
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp", "inbox ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here
                        if (!color_background.isShown()) {
                            descriptionText.setVisibility(View.GONE);
                            buttonsContainer.setVisibility(View.GONE);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp", "inbox ACTION_UP");


                        if (color_background.isShown()) {
                            resetComments();
                        }
                        showButtonsAndBackground(true);
                }
                return false;
            }
        });

        return rootView;
    }

    private void loadVotesOffline(){
        Log.d("myapp", "**--loadOffline:");
        if((listRed.size() + listGreen.size() + listYellow.size() ) == 0) {
            Context ctx = AppController.getInstance().getApplicationContext();
            List<Comment> comments = new ModelCache<List<Comment>>().loadModel(ctx,new TypeToken<List<Comment>>(){}.getType(), Global.OFFLINE_VOTE+id);
            if(comments != null && comments.size() > 0 && comments.get(0) instanceof Comment) {
                Log.d("myapp", "**--loadOffline: inside ");
                sortVotes(comments);
            }
        }
    }

    private void sortVotes(List<Comment> comments){
        listRed.clear();
        listYellow.clear();
        listGreen.clear();
        for(Comment c: comments){
            switch (c.getRate()){
                case Global.RED:
                    listRed.add(c);
                    break;
                case Global.YELLOW:
                    listYellow.add(c);
                    break;
                case Global.GREEN:
                    listGreen.add(c);
                    break;
            }
        }
        sortDesc(listRed);
        sortDesc(listYellow);
        sortDesc(listGreen);

        resetComments();
    }

    public static void sortDesc(List< Comment > list){
        Collections.sort(list, new Comparator<Comment>() {
            @Override
            public int compare(Comment obj1, Comment obj2) {
                return obj2.getDateSent().compareToIgnoreCase(obj1.getDateSent());
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        state = new ListenerPagerStateChange() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //voltamos a mostrar as opções
                    Log.d("myapp2", "**--inboxpage  :" + state);
                    showButtonsAndBackground(true);
                    resetComments();
                }
            }
        };

        ((OutBoxFragment)getParentFragment()).addStateChange(state);
    }

    @Override
    public void onPause(){
        super.onPause();
        ((OutBoxFragment)getParentFragment()).removeStateChange(state);
    }

    public void showButtonsAndBackground(boolean show){

        descriptionText.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        color_background.setVisibility(show ? View.GONE : View.VISIBLE);
        comment_layout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void resetComments(){
        botaoVermelho.setText(listRed.size()+"");
        botaoVerde.setText(listGreen.size() + "");
        botaoAmarelo.setText(listYellow.size() + "");
        selectdList = -1;
        selectedPos = -1;
    }


    private void openComment(String author, String date, int vote){
        Comment comment = null;
        List<Comment> comments = new ArrayList<>();
        switch (vote){
            case Global.RED:
                comments =  listRed;
                break;
            case Global.YELLOW:
                comments = listYellow;
                break;
            case Global.GREEN:
                comments = listGreen;
                break;
        }
        selectdList = vote;
        int pos = -1;
        for (Comment c: comments) {
            pos++;
            if(c.getDateSent().equals(date) && c.getName().equals(author)){
                selectedPos = pos;
                comment = c;
                break;
            }
        }

        if(comment != null && selectdList >= 0){
            commentText.setText(comment.getComment());
            comment_user.setText(comment.getName() + " - " + comment.getDateSent());
            getButton(selectdList).setText((selectedPos + 1) + "/" + comments.size());
            color_background.setBackgroundColor(getResources().getColor(getColor(selectdList)));
            Log.d("myapp", "getNext end");
            color_background.setVisibility(View.VISIBLE);
            comment_layout.setVisibility(View.VISIBLE);
        }



    }



    private void getNext(int list){
        Log.d("myapp", "getNext:" + list);
        if(list != selectdList){
            resetComments();
            Log.d("myapp", "list != selectdList:");
            selectdList = list;
            selectedPos = -1;

        }
        selectedPos++;



        //dar a volta à lista
        if(getList(list).size() <= selectedPos)
            selectedPos = 0;
        Log.d("myapp", "getNext start");

        if(getList(list).size() == 0)
            return;

        List<Comment> commentList = getList(list);
        Comment comment = commentList.get(selectedPos);
        commentText.setText(comment.getComment());
        comment_user.setText(comment.getName() + " - " + comment.getDateSent());
        getButton(list).setText((selectedPos + 1) + "/" + commentList.size());
        color_background.setBackgroundColor(getResources().getColor(getColor(list)));
        Log.d("myapp", "getNext end");
        color_background.setVisibility(View.VISIBLE);
        comment_layout.setVisibility(View.VISIBLE);

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

    private Button getButton(int order){
        Button button = null;
        switch (order){
            case Global.RED:
                button =  botaoVermelho;
                break;
            case Global.YELLOW:
                button = botaoAmarelo;
                break;
            case Global.GREEN:
                button = botaoVerde;
                break;

        }
        return button;
    }

    private List<Comment> getList(int order){
        List<Comment> list = null;
        switch (order){
            case Global.RED:
                list =  listRed;

                break;
            case Global.YELLOW:
                list = listYellow;

                break;
            case Global.GREEN:
                list = listGreen;

                break;
        }
        return list;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botaoVerde:
                getNext(Global.GREEN);
                break;
            case R.id.botaoAmarelo:
                getNext(Global.YELLOW);
                break;
            case R.id.botaoVermelho:
                getNext(Global.RED);
                break;
        }
    }
}