package com.tappitz.tappitz.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;

public class InBoxPageFragment extends Fragment implements View.OnClickListener {

    private View rootView, layout_vote;
    private TextView textViewOwner, textViewDate;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private EditText editTextComment;


    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde};
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public static InBoxPageFragment newInstance(Bundle args) {
        InBoxPageFragment fragment = new InBoxPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_inbox_child, container, false);
        NetworkImageView imageView = (NetworkImageView)rootView.findViewById(R.id.picture);

        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        String text = getArguments().getString(Global.TEXT_RESOURCE);
        String id = getArguments().getString(Global.ID_RESOURCE);
        String owner = getArguments().getString(Global.OWNER_RESOURCE);
        String date = getArguments().getString(Global.DATE_RESOURCE);
        String myComment = getArguments().getString(Global.MYCOMMENT_RESOURCE);

        boolean hasVoted = getArguments().getBoolean(Global.HAS_VOTED_RESOURCE);
        int choice = getArguments().getInt(Global.CHOICE_RESOURCE);


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

        editTextComment = (EditText)rootView.findViewById(R.id.editTextComment);
        layout_vote = rootView.findViewById(R.id.layout_vote);
        TextView textview = (TextView) rootView.findViewById(R.id.photo_description);
        textview.setText(text);

        TextView textViewVoted = (TextView) rootView.findViewById(R.id.photo_description_voted);
        textViewVoted.setText(text);

        textViewOwner = (TextView) rootView.findViewById(R.id.textViewOwner);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);

        textViewOwner.setText(owner);
        textViewDate.setText(date);
        Log.d("myapp", "inbox textViewDate: " + date);

        if(hasVoted){
            rootView.findViewById(R.id.layout_vote).setVisibility(View.GONE);
            rootView.findViewById(R.id.layout_already_voted).setVisibility(View.VISIBLE);
            color_background.setVisibility(View.VISIBLE);
            color_background.setBackgroundColor(getResources().getColor(getColor(choice)));

        }else {
            rootView.findViewById(R.id.layout_already_voted).setVisibility(View.GONE);
            color_background.setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.layout_vote).setVisibility(View.VISIBLE);
        }



        //Picasso.with(context).load(url).fit().into(imageView, call);


        //Picasso.with(context).load(url).fit().memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
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

        rootView.findViewById(R.id.background).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp", "inbox ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here
                        layout_vote.setVisibility(View.INVISIBLE);
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);


                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        layout_vote.setVisibility(View.VISIBLE);
                        //=====Write down you code Finger Released code here

                        return true;
                }

                return false;
            }
        });





        return rootView;
    }


    private void dummyListComments(){
//        List<Comment> comments = new ArrayList<>();
//        comments.add(new Comment(Global.RED, "Não é bonito", "04-10-2015", "João Sampaio"));
//        comments.add(new Comment(Global.YELLOW, "É razoavel", "04-10-2015", "Jorge A."));
//        comments.add(new Comment(Global.RED, "texto, texto, é feio, bla bla", "05-10-2015", "João Sampaio"));
//        comments.add(new Comment(Global.GREEN, "Muito bom, continua!", "05-10-2015", "João Sampaio"));
//        comments.add(new Comment(Global.GREEN, "Adorei!!!!!", "05-10-2015", "Marisa S."));
//        comments.add(new Comment(Global.YELLOW, "lindo.", "06-10-2015", "João Sampaio"));
//
//        for(Comment c: comments){
//            switch (c.getRate()){
//                case Global.RED:
//                    listRed.add(c);
//                    break;
//                case Global.YELLOW:
//                    listYellow.add(c);
//                    break;
//                case Global.GREEN:
//                    listGreen.add(c);
//                    break;
//            }
//        }
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botaoVerde:

                break;
            case R.id.botaoAmarelo:

                break;
            case R.id.botaoVermelho:

                break;

        }
        //Toast.makeText(v.getContext(), "Clicked Position: " + position, Toast.LENGTH_LONG).show();
    }
}