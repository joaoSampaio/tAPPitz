package com.tappitz.tappitz.ui.secondary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.background.BackgroundService;
import com.tappitz.tappitz.model.FutureVote;
import com.tappitz.tappitz.model.ReceivedPhoto;
import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.ui.ScreenSlidePagerActivity;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.ModelCache;
import com.tappitz.tappitz.util.VerticalViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class QRCodeDialogFragment extends DialogFragment implements View.OnClickListener, View.OnLongClickListener {

    private View rootView, layout_vote, layout_container, layoutVoteText, painelvotacao, layout_already_voted_original, loading;
    private TextView textViewOwner, yourComment;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private EditText editTextComment;
    private int id, currentVote;
    private ListenerPagerStateChange state;
    String text;
    private boolean hasVoted, isTemporary;

    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde, R.id.buttonBack, R.id.buttonSend};


    public static QRCodeDialogFragment newInstance(Bundle args) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_inbox_child, container, false);

        ImageView imageView = (ImageView)rootView.findViewById(R.id.picture);
        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        text = getArguments().getString(Global.TEXT_RESOURCE);
        id = getArguments().getInt(Global.ID_RESOURCE);
        String owner = getArguments().getString(Global.OWNER_RESOURCE);
        String dateSentTimeAgo = getArguments().getString(Global.DATE_RESOURCE);


        String myComment = getArguments().getString(Global.MYCOMMENT_RESOURCE);

        hasVoted = getArguments().getBoolean(Global.HAS_VOTED_RESOURCE);
        isTemporary = getArguments().getBoolean(Global.IS_TEMPORARY_RESOURCE);


        rootView.findViewById(R.id.textViewTemp).setVisibility(isTemporary? View.VISIBLE: View.GONE);


        String dateVoteTimeAgo = "";
        int choice = getArguments().getInt(Global.CHOICE_RESOURCE);

        loading = rootView.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        loading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        for(int idBtn: CLICKABLE) {
            rootView.findViewById(idBtn).setOnClickListener(this);
            rootView.findViewById(idBtn).setOnLongClickListener(this);
        }

        painelvotacao = rootView.findViewById(R.id.painelvotacao);
        layoutVoteText = rootView.findViewById(R.id.layoutVoteText);
        botaoVerde = (Button)rootView.findViewById(R.id.botaoVerde);
        botaoAmarelo = (Button)rootView.findViewById(R.id.botaoAmarelo);
        botaoVermelho = (Button)rootView.findViewById(R.id.botaoVermelho);
        color_background = (ImageView)rootView.findViewById(R.id.color_background);
        layout_already_voted_original = rootView.findViewById(R.id.layout_already_voted_original);

        editTextComment = (EditText)rootView.findViewById(R.id.editTextComment);
        layout_vote = rootView.findViewById(R.id.layout_vote);
        layout_container = rootView.findViewById(R.id.container);
        yourComment = (TextView)rootView.findViewById(R.id.yourComment);
        yourComment.setText("\""+myComment);
        TextView textview = (TextView) rootView.findViewById(R.id.photo_description);
        textview.setText("\""+text);
        TextView textViewVoted = (TextView) rootView.findViewById(R.id.photo_description_voted);
        textViewVoted.setText("\""+text);
        textViewOwner = (TextView) rootView.findViewById(R.id.textViewOwner);

        textViewOwner.setText(owner + " - " + dateSentTimeAgo);
        ((TextView) rootView.findViewById(R.id.textViewOwner3)).setText(owner + " - " + dateSentTimeAgo);
        if(hasVoted)
            ((TextView) rootView.findViewById(R.id.textViewOwner2)).setText("You - " + dateVoteTimeAgo);
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

        rootView.findViewById(R.id.picture).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        Log.d("myapp", "inbox ACTION_DOWN");
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp", "inbox ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here
                        showButtonsAndBackground(false);
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp", "inbox ACTION_UP");
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
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
                    dismiss();
                    ((ScreenSlidePagerActivity)getActivity()).enableQRCodeCapture(true);
                    return true;
                }
                // Otherwise, do nothing else
                else return false;
            }
        });

    }



    @Override
    public void onPause(){
        super.onPause();
        if(getDialog() != null)
            getDialog().dismiss();

    }

    public void showButtonsAndBackground(boolean show){
        if(layout_container != null) {
            Log.d("myapp", "showButtonsAndBackground show: " + show + "hasVoted:"+ hasVoted);
            layout_container.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            layout_already_voted_original.setVisibility((!show && hasVoted) ? View.VISIBLE : View.INVISIBLE);
        }
        else
            Log.d("myapp", "layout_container null");
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
                toggleButtons(false);
                sendVote(Global.GREEN);
                break;
            case R.id.botaoAmarelo:
                toggleButtons(false);
                sendVote(Global.YELLOW);
                break;
            case R.id.botaoVermelho:
                toggleButtons(false);
                sendVote(Global.RED);
                break;
            case R.id.buttonBack:
                showSendExtras(false);
                closeKeyboard();
                break;
            case R.id.buttonSend:
                closeKeyboard();
                sendVote(currentVote);

                break;

        }
        //Toast.makeText(v.getContext(), "Clicked Position: " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case R.id.botaoVerde:
                currentVote = Global.GREEN;
                break;
            case R.id.botaoAmarelo:
                currentVote = Global.YELLOW;
                break;
            case R.id.botaoVermelho:
                currentVote = Global.RED;
                break;

        }

        color_background.setBackgroundColor(getResources().getColor(getColor(currentVote)));
        color_background.setVisibility(View.VISIBLE);
        showSendExtras(true);

        editTextComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextComment, InputMethodManager.SHOW_IMPLICIT);

        return true;
    }

    private void showSendExtras(boolean show){
        editTextComment.setVisibility(show? View.VISIBLE : View.GONE);
        layoutVoteText.setVisibility(show ? View.VISIBLE : View.GONE);
        painelvotacao.setVisibility(show? View.INVISIBLE : View.VISIBLE);
        color_background.setVisibility(show ? View.VISIBLE : View.GONE);
        botaoAmarelo.setEnabled(!show);
        botaoVerde.setEnabled(!show);
        botaoVermelho.setEnabled(!show);

    }

    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void sendVote(final int vote){

        showTemporary(vote);
        getDialog().dismiss();
        ((ScreenSlidePagerActivity)getActivity()).enableQRCodeCapture(true);



    }



    public void showTemporary(int vote){
        botaoVermelho.setEnabled(false);
        botaoAmarelo.setEnabled(false);
        botaoVerde.setEnabled(false);
        botaoVermelho.setVisibility(View.INVISIBLE);
        botaoAmarelo.setVisibility(View.INVISIBLE);
        botaoVerde.setVisibility(View.INVISIBLE);


        color_background.setBackgroundColor(getResources().getColor(getColor(vote)));
        color_background.setVisibility(View.VISIBLE);
    }


    private void toggleButtons(boolean enable){
        botaoVerde.setEnabled(enable);
        botaoAmarelo.setEnabled(enable);
        botaoVermelho.setEnabled(enable);
    }

}