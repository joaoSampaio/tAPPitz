package com.tappitz.tappitz.ui.secondary;

import android.content.Context;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Comment;
import com.tappitz.tappitz.rest.RestClient;
import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.ui.OutBoxFragment;
import com.tappitz.tappitz.util.ListenerPagerStateChange;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class OutBoxPageFragment extends Fragment implements View.OnClickListener {

    private View rootView, comment_layout;
    private TextView commentText, comment_user, descriptionText;
    private List<Comment> listGreen, listRed, listYellow;
    private int selectdList, selectedPos;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private LinearLayout buttonsContainer;
    private ListenerPagerStateChange state;


    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde};
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public static OutBoxPageFragment newInstance(Bundle args) {
        OutBoxPageFragment fragment = new OutBoxPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_outbox_child, container, false);
        NetworkImageView imageView = (NetworkImageView)rootView.findViewById(R.id.picture);

        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        String text = getArguments().getString(Global.TEXT_RESOURCE);
        int id = getArguments().getInt(Global.ID_RESOURCE);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        imageView.setImageUrl(url, imageLoader);

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

        RestClient.getService().getOutboxComments(id, new retrofit.Callback<List<Comment>>() {
            @Override
            public void success(List<Comment> comments, Response response) {
                dummyListComments();
            }

            @Override
            public void failure(RetrofitError error) {
                dummyListComments();
            }
        });

        rootView.findViewById(R.id.picture).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        Log.d("myapp", "inbox ACTION_DOWN");
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp", "inbox ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here
                        if(!color_background.isShown()) {
                            descriptionText.setVisibility(View.GONE);
                            buttonsContainer.setVisibility(View.GONE);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp", "inbox ACTION_UP");


                        if(color_background.isShown()){
//                            color_background.setVisibility(View.GONE);
//                            comment_layout.setVisibility(View.GONE);
                            resetComments();
                        }
//                        descriptionText.setVisibility(View.VISIBLE);
//                        buttonsContainer.setVisibility(View.VISIBLE);
                        showButtonsAndBackground(true);


                    case MotionEvent.ACTION_POINTER_UP:
                        //=====Write down you code Finger Released code here

                }
                return false;
            }
        });

        return rootView;
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

        descriptionText.setVisibility(show? View.VISIBLE : View.GONE);
        buttonsContainer.setVisibility(show? View.VISIBLE : View.GONE);
        color_background.setVisibility(show? View.GONE : View.VISIBLE);
        comment_layout.setVisibility(show? View.GONE : View.VISIBLE);
    }




    private void dummyListComments(){
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(Global.RED, "Não é bonito", "04-10-2015", "João Sampaio"));
        comments.add(new Comment(Global.YELLOW, "É razoavel", "04-10-2015", "Jorge A."));
        comments.add(new Comment(Global.RED, "texto, texto, é feio, bla bla", "05-10-2015", "João Sampaio"));
        comments.add(new Comment(Global.GREEN, "Muito bom, continua!", "05-10-2015", "João Sampaio"));
        comments.add(new Comment(Global.GREEN, "Adorei!!!!!", "05-10-2015", "Marisa S."));
        comments.add(new Comment(Global.YELLOW, "lindo.", "06-10-2015", "João Sampaio"));

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

        resetComments();




    }

    private void resetComments(){
        botaoVermelho.setText(listRed.size()+"");
        botaoVerde.setText(listGreen.size()+"");
        botaoAmarelo.setText(listYellow.size()+"");
        selectdList = -1;
        selectedPos = -1;
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
//            case R.id.picture:
//                Log.d("myapp", "containercontainer");
//                color_background.setVisibility(View.GONE);
//                comment_layout.setVisibility(View.GONE);
//                resetComments();
//                break;

        }
        //Toast.makeText(v.getContext(), "Clicked Position: " + position, Toast.LENGTH_LONG).show();
    }
}