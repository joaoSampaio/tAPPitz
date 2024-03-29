package com.tappitz.app.ui.secondary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.app.AppController;
import com.tappitz.app.background.BackgroundService;
import com.tappitz.app.model.FutureVote;
import com.tappitz.app.model.ReceivedPhoto;
import com.tappitz.app.ui.InBoxFragment;
import com.tappitz.app.ui.MainActivity;
import com.tappitz.app.util.ListenerPagerStateChange;
import com.tappitz.app.util.ModelCache;
import com.tappitz.app.util.VerticalViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconTextView;


public class InBoxPageFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private View rootView, layout_vote, layout_container, layoutVoteText, painelvotacao, layout_already_voted_original, loading, layoutText, descriptionContainer;
    private TextView textViewOwner, yourComment;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background, emoji_btn;
    private EmojiconEditText editTextComment;
    private int id, currentVote;
    private ListenerPagerStateChange state;
    String text;
    private boolean hasVoted, isTemporary, isGif;

    private final static int[] CLICKABLE = {R.id.botaoVermelho, R.id.botaoAmarelo, R.id.botaoVerde, R.id.buttonBack, R.id.buttonSend};


    public static InBoxPageFragment newInstance(Bundle args) {
        InBoxPageFragment fragment = new InBoxPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_inbox_child, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.picture);
        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        id = getArguments().getInt(Global.ID_RESOURCE);

        text = getArguments().getString(Global.TEXT_RESOURCE);
        isGif = getArguments().getBoolean(Global.IS_GIF);
        String owner = getArguments().getString(Global.OWNER_RESOURCE);
        String dateSentTimeAgo = getArguments().getString(Global.DATE_RESOURCE);


        String myComment = getArguments().getString(Global.MYCOMMENT_RESOURCE);

        hasVoted = getArguments().getBoolean(Global.HAS_VOTED_RESOURCE);
        isTemporary = getArguments().getBoolean(Global.IS_TEMPORARY_RESOURCE);



        if(isTemporary && !BackgroundService.isWifiAvailable()){
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
//        rootView.findViewById(R.id.textViewTemp).setVisibility(isTemporary? View.VISIBLE: View.GONE);


        String dateVoteTimeAgo = "";
        if(hasVoted)
            dateVoteTimeAgo = getArguments().getString(Global.VOTE_DATE_RESOURCE);
        int choice = getArguments().getInt(Global.CHOICE_RESOURCE);

        loading = rootView.findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        if(isGif){
            Glide.with(this)
                    .load(url)
                    .asGif()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.IMMEDIATE)
                    .listener(new RequestListener<String, GifDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                            if(e != null)
                                Log.d("glide", "exception->"+e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        }else {
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
        }
        for(int idBtn: CLICKABLE) {
            rootView.findViewById(idBtn).setOnClickListener(this);
            rootView.findViewById(idBtn).setOnLongClickListener(this);
        }

//        ScrollView sv = (ScrollView)rootView.findViewById(R.id.layoutVoteText);
//        sv.setEnabled(false);

        painelvotacao = rootView.findViewById(R.id.painelvotacao);
        layoutVoteText = rootView.findViewById(R.id.layoutVoteText);
        botaoVerde = (Button)rootView.findViewById(R.id.botaoVerde);
        botaoAmarelo = (Button)rootView.findViewById(R.id.botaoAmarelo);
        botaoVermelho = (Button)rootView.findViewById(R.id.botaoVermelho);
        color_background = (ImageView)rootView.findViewById(R.id.color_background);
        layout_already_voted_original = rootView.findViewById(R.id.layout_already_voted_original);
        layoutText = rootView.findViewById(R.id.layoutText);
        editTextComment = (EmojiconEditText)rootView.findViewById(R.id.editTextComment);
        descriptionContainer = rootView.findViewById(R.id.descriptionContainer);
        emoji_btn = (ImageView)rootView.findViewById(R.id.emoji_btn);
        layout_vote = rootView.findViewById(R.id.layout_vote);
        layout_container = rootView.findViewById(R.id.container);
        yourComment = (TextView)rootView.findViewById(R.id.yourComment);
        yourComment.setText( ((myComment!= null && myComment.length() > 0)? ("\"" + myComment) : ""));

        TextView textview = (EmojiconTextView) rootView.findViewById(R.id.photo_description);
        textview.setText( ((text!= null && text.length() > 0)? ("\"" + text) : ""));
        TextView textViewVoted = (TextView) rootView.findViewById(R.id.photo_description_voted);
        textViewVoted.setText( ((text!= null && text.length() > 0)? ("\"" + text) : ""));
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

        editTextComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard();
                }else{
                    changeBackButtonPosition(false);
                }

            }
        });

        rootView.findViewById(R.id.picture).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_POINTER_DOWN:
                        showButtonsAndBackground(false);
                        return true;

                    case MotionEvent.ACTION_UP:
                        showButtonsAndBackground(true);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //=====Write down you code Finger Released code here
                        break;


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
                    showButtonsAndBackground(true);
                }
            }
        };

        ((InBoxFragment)getParentFragment()).addStateChange(state);
    }

    @Override
    public void onPause(){
        super.onPause();
        ((InBoxFragment)getParentFragment()).removeStateChange(state);
    }

    public void showButtonsAndBackground(boolean show){
        if(layout_container != null) {
            layout_container.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            layout_already_voted_original.setVisibility((!show && hasVoted) ? View.VISIBLE : View.INVISIBLE);
        }

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

        ((MainActivity)getActivity()).getEmojiManager().setEmojiconEditText(editTextComment);
        ((MainActivity)getActivity()).getEmojiManager().setEmojiButton(emoji_btn);

        color_background.setBackgroundColor(getResources().getColor(getColor(currentVote)));
        color_background.setVisibility(View.VISIBLE);
        showSendExtras(true);

        editTextComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextComment, InputMethodManager.SHOW_IMPLICIT);
        changeBackButtonPosition(false);
        return true;
    }

    private void showSendExtras(boolean show){

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if(show){
            p.addRule(RelativeLayout.ABOVE, R.id.layoutText);
        }else {
            p.addRule(RelativeLayout.ABOVE, R.id.painelvotacao);
        }

        descriptionContainer.setLayoutParams(p);

        layoutText.setVisibility(show? View.VISIBLE : View.GONE);
//        editTextComment.setVisibility(show? View.VISIBLE : View.GONE);
        layoutVoteText.setVisibility(show ? View.VISIBLE : View.GONE);
        painelvotacao.setVisibility(show? View.INVISIBLE : View.VISIBLE);
        color_background.setVisibility(show ? View.VISIBLE : View.GONE);
        botaoAmarelo.setEnabled(!show);
        botaoVerde.setEnabled(!show);
        botaoVermelho.setEnabled(!show);

    }

    private void changeBackButtonPosition(boolean top){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if(top){
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }else {
            p.addRule(RelativeLayout.CENTER_VERTICAL);
        }

        layoutVoteText.setLayoutParams(p);
    }

    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        changeBackButtonPosition(true);
    }

    private void animatePagerTransition(final boolean forward, final VerticalViewPager viewPager) {

        ValueAnimator animator = ValueAnimator.ofInt(0, viewPager.getWidth());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewPager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                viewPager.endFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                viewPager.fakeDragBy(dragOffset * (forward ? -1 : 1));
            }
        });

        animator.setDuration(500);
        viewPager.beginFakeDrag();
        animator.start();
    }

    private void sendVote(final int vote){

        showTemporary(vote);

        final String comment = editTextComment.isShown()? editTextComment.getText().toString() : "";

        List<ReceivedPhoto> tmp = new ModelCache<List<ReceivedPhoto>>().loadModel(getActivity(),new TypeToken<List<ReceivedPhoto>>(){}.getType(), Global.OFFLINE_INBOX);
        if(tmp != null && tmp.size() > 0 && tmp.get(0) instanceof ReceivedPhoto) {
            ReceivedPhoto photo = ReceivedPhoto.getPhotoWithId(tmp, id);
            if(photo != null) {
                photo.setVote(vote);
                photo.setComment(comment);
                photo.setHasVoted(true);
                photo.setIsVoteTemporary(true);
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                photo.setVotedDate(now);
                new ModelCache<List<ReceivedPhoto>>().saveModel(getActivity(), tmp, Global.OFFLINE_INBOX);

            }
        }
        //notifica o adapter
        if(getActivity() != null && getParentFragment() != null) {
            ((InBoxFragment)getParentFragment()).loadOffline();
        }

        FutureVote voteWork = new FutureVote(id, comment, vote);
        BackgroundService.addVoteWork(voteWork);
        //lançar o serviço
        AppController.getAppContext().startService(new Intent(AppController.getAppContext(), BackgroundService.class));

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