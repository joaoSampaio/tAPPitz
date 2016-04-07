package com.tappitz.tappitz.ui.secondary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.adapter.OutBoxCommentAdapter;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.background.BackgroundService;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.ListVotesService;
import com.tappitz.tappitz.ui.OutBoxFragment;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OutBoxPageFragment extends Fragment implements View.OnClickListener {

    private View rootView, comment_layout, layout_out_description, action_go_back;
    private TextView commentText, descriptionText;
    private List<Comment> listGreen, listRed, listYellow;
    private int selectdList, selectedPos, id;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private LinearLayout buttonsContainer;
    private ListenerPagerStateChange state;
    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde, R.id.action_go_back};
    private OutBoxPageFragment $this = this;
    private RecyclerView commentList;
    private OutBoxCommentAdapter adapter;
    private List<String> comments;
    private View.OnTouchListener touch;

    ImageView image;
    public static OutBoxPageFragment newInstance(Bundle args) {
        OutBoxPageFragment fragment = new OutBoxPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_outbox_child, container, false);

        boolean isTemporary = getArguments().getBoolean(Global.IS_TEMPORARY_RESOURCE);
        final String imagePath = getArguments().getString(Global.TEMP_FINAL_RESOURCE);
        final String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        String text = getArguments().getString(Global.TEXT_RESOURCE);
        String dateSentTimeAgo = getArguments().getString(Global.DATE_RESOURCE);
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
        commentList = (RecyclerView)rootView.findViewById(R.id.commentList);
        buttonsContainer = (LinearLayout)rootView.findViewById(R.id.painelvotacao);
        layout_out_description  = rootView.findViewById(R.id.layout_out_description);
        action_go_back = rootView.findViewById(R.id.action_go_back);
        descriptionText = (TextView) rootView.findViewById(R.id.photo_description);
        Log.d("myapp", "out dateSentTimeAgo:" + dateSentTimeAgo);
        descriptionText.setText("You - " + dateSentTimeAgo  + ((text.length() > 0) ? ( "\n" +"\"" + text) : ""));

//        commentText = (TextView) rootView.findViewById(R.id.photo_comment);

        rootView.findViewById(R.id.action_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeListVote();
            }
        });
        image = (ImageView) rootView.findViewById(R.id.picture);
        Log.d("myapp", "out isTemporary:" + isTemporary);
        if(!isTemporary) {

            loadVotesOffline();

            Glide.with($this)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.d("myapp", "out onException" + e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .override(AppController.getInstance().width, AppController.getInstance().height)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .into((ImageView) rootView.findViewById(R.id.picture));

        }else {

            if(!BackgroundService.isWifiAvailable()){
                rootView.findViewById(R.id.textViewTemp).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.textViewTemp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(BackgroundService.isWifiAvailable()) {
                            AppController.getAppContext().startService(new Intent(AppController.getAppContext(), BackgroundService.class));
                        }else{
                            Toast.makeText(AppController.getAppContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            Glide.with($this)
                    .load(imagePath)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .override(AppController.getInstance().width, AppController.getInstance().height)
                    .into((ImageView) rootView.findViewById(R.id.picture));

        }
        touch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_CANCEL:
                        Log.d("myapp", "inbox ACTION_CANCEL");
                        //showButtonsAndBackground(true);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("myapp", "inbox ACTION_DOWN");
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp", "inbox ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here
                        if (!color_background.isShown()) {
                            layout_out_description.setVisibility(View.GONE);
                            action_go_back.setVisibility(View.GONE);
//                            descriptionText.setVisibility(View.GONE);
                            buttonsContainer.setVisibility(View.GONE);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        showButtonsAndBackground(true);
                }
                return false;
            }
        };
        rootView.findViewById(R.id.picture).setOnTouchListener(touch);

        comments = new ArrayList<>();
        adapter = new OutBoxCommentAdapter(getActivity(), comments);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentList.setLayoutManager(layoutManager);
        commentList.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), false));
        commentList.setAdapter(adapter);

        return rootView;
    }

    private void loadVotesOffline(){
        if((listRed.size() + listGreen.size() + listYellow.size() ) == 0) {
            Context ctx = AppController.getInstance().getApplicationContext();
            List<Comment> comments = new ModelCache<List<Comment>>().loadModel(ctx,new TypeToken<List<Comment>>(){}.getType(), Global.OFFLINE_VOTE+id);
            if(comments != null && comments.size() > 0 && comments.get(0) instanceof Comment) {
                sortVotes(comments);
            }
            if(comments == null)
                comments = new ArrayList<>();
            if(comments.size() == 0){
                new ListVotesService(id, new CallbackMultiple<List<Comment>, String>() {
                    @Override
                    public void success(List<Comment> comments) {
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

        showNumVotes();
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
                    Log.d("myapp2", "**--outboxpage  :" + state);
                    showButtonsAndBackground(true);
                    showNumVotes();
                }
            }
        };

        ((OutBoxFragment)getParentFragment()).addStateChange(state);
    }

    @Override
    public void onPause(){
        super.onPause();
        ((OutBoxFragment)getParentFragment()).removeStateChange(state);
        Glide.clear((ImageView)rootView.findViewById(R.id.picture));
    }

    public void showButtonsAndBackground(boolean show){

        layout_out_description.setVisibility(show ? View.VISIBLE : View.GONE);
        action_go_back.setVisibility(show ? View.VISIBLE : View.GONE);
//        descriptionText.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        color_background.setVisibility(show ? View.GONE : View.VISIBLE);
        comment_layout.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private void showNumVotes(){
        botaoVermelho.setText(listRed.size()+"");
        botaoVerde.setText(listGreen.size() + "");
        botaoAmarelo.setText(listYellow.size() + "");
    }




    private void showVoteList(int list){
        List<Comment> commentList = getList(list);
//        commentList.add(new Comment(0, "coment", "10-04-2015 12:20", "Joao"));
//        commentList.add(new Comment(0, "coment1", "10-04-2015 12:20", "Joao"));
//        commentList.add(new Comment(0, "coment2", "10-04-2015 12:20", "Joao"));
//        commentList.add(new Comment(0, "coment3", "10-04-2015 12:20", "Joao"));
//        commentList.add(new Comment(0, "coment4", "10-04-2015 12:20", "Joao"));
//        commentList.add(new Comment(0, "coment5", "10-04-2015 12:20", "Joao"));
//        commentList.add(new Comment(0, "coment6", "10-04-2015 12:20", "Joao"));
        if(commentList.size() == 0)
            return;
        if(list == selectdList){
            closeListVote();
            return;
        }
        rootView.findViewById(R.id.picture).setOnTouchListener(null);
        selectdList = list;

        comments.clear();
        String allComments = "";


        for (Comment c: commentList) {
            allComments = "";
            allComments += c.getName() + " - " + c.getTimeAgo() + "\n";
            if(c.getComment().length() > 0){
                allComments += "\"" + c.getComment() + "\n";
            }
            allComments +=  "\n";
            comments.add(allComments);
            adapter.notifyDataSetChanged();
        }

//        commentText.setText(allComments);
        color_background.setBackgroundColor(getResources().getColor(getColor(list)));
        color_background.setVisibility(View.VISIBLE);
        comment_layout.setVisibility(View.VISIBLE);

    }

    private void closeListVote(){
        rootView.findViewById(R.id.picture).setOnTouchListener(touch);
        showButtonsAndBackground(true);
        selectdList = -1;
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
                showVoteList(Global.GREEN);
                break;
            case R.id.botaoAmarelo:
                showVoteList(Global.YELLOW);
                break;
            case R.id.botaoVermelho:
                showVoteList(Global.RED);
                break;
            case R.id.action_go_back:
                ((ScreenSlidePagerActivity)getActivity()).showPage(Global.HOME);
                break;
        }
    }
}