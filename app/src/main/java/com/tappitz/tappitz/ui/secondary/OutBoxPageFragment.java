package com.tappitz.tappitz.ui.secondary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class OutBoxPageFragment extends Fragment implements View.OnClickListener {

    private View rootView, comment_layout;
    private TextView commentText, comment_user, comment_date;
    private List<Comment> listGreen, listRed, listYellow;
    private int selectdList, selectedPos;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;


    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde, R.id.picture};
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public static OutBoxPageFragment newInstance(Bundle args) {
        OutBoxPageFragment fragment = new OutBoxPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment_screen_slide_page, container, false);


        //position = getArguments().getInt(POSITION_KEY);

        rootView = inflater.inflate(R.layout.fragment_outbox_child, container, false);
        NetworkImageView imageView = (NetworkImageView)rootView.findViewById(R.id.picture);

        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        String text = getArguments().getString(Global.TEXT_RESOURCE);
        int id = getArguments().getInt(Global.ID_RESOURCE);


        Log.d("myapp", "url******" + url);
        Log.d("myapp", "url******" + url);
        Log.d("myapp", "url******" + url);
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

//        Picasso.with(context).load(url).fit().into(imageView, call);


//        Picasso.with(getActivity()).load(url).fit().memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
//        int[] measures = ((MainActivity)getActivity()).getContainerSize();
//        int MAX_WIDTH = measures[0];
//        int MAX_HEIGHT = measures[1];
//        Log.d("myapp", "MAX_WIDTH2: " + MAX_WIDTH + " MAX_HEIGHT2: " + MAX_HEIGHT);
//
//        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
        // Loads given image
//        Picasso.with(imageView.getContext())
//                .load(url)
//                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                .skipMemoryCache()
//                .resize(size, size)
//                .centerInside()
//                .into(imageView);



        TextView textview = (TextView) rootView.findViewById(R.id.photo_description);
        textview.setText(text);
        //imageView.setOnClickListener(this);

        commentText = (TextView) rootView.findViewById(R.id.photo_comment);
        comment_date = (TextView) rootView.findViewById(R.id.comment_date);
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

        return rootView;
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
    }


    private void getNext(int list){
        Log.d("myapp", "getNext:" + list);
        if(list != selectdList){
            Log.d("myapp", "list != selectdList:");
            selectdList = list;
            selectedPos = -1;
            resetComments();
        }
        selectedPos++;

        //dar a volta à lista
        if(getList(list).size() <= selectedPos)
            selectedPos = 0;
        Log.d("myapp", "getNext start");
        List<Comment> commentList = getList(list);
        Comment comment = commentList.get(selectedPos);
        commentText.setText(comment.getComment());
        comment_user.setText(comment.getName());
        comment_date.setText(comment.getDateSent());
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
            case R.id.picture:
                Log.d("myapp", "containercontainer");
                color_background.setVisibility(View.GONE);
                comment_layout.setVisibility(View.GONE);
                resetComments();
                break;

        }
        //Toast.makeText(v.getContext(), "Clicked Position: " + position, Toast.LENGTH_LONG).show();
    }
}