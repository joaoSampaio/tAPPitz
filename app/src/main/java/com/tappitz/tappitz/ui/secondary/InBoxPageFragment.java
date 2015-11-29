package com.tappitz.tappitz.ui.secondary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.rest.model.PhotoInbox;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.SendVotePictureService;
import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.util.ListenerPagerStateChange;
import com.tappitz.tappitz.util.VerticalViewPager;


public class InBoxPageFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private View rootView, layout_vote, layout_container;
    private TextView textViewOwner, textViewDate, yourComment;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private EditText editTextComment;
    private int id;
    private ListenerPagerStateChange state;
    String text;

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
        ImageView imageView = (ImageView)rootView.findViewById(R.id.picture);

        Log.d("myapp", "inbox Redraw.............................");

        String url = getArguments().getString(Global.IMAGE_RESOURCE_URL);
        text = getArguments().getString(Global.TEXT_RESOURCE);
        id = getArguments().getInt(Global.ID_RESOURCE);
        String owner = getArguments().getString(Global.OWNER_RESOURCE);
        String date = getArguments().getString(Global.DATE_RESOURCE);
        String myComment = getArguments().getString(Global.MYCOMMENT_RESOURCE);

        boolean hasVoted = getArguments().getBoolean(Global.HAS_VOTED_RESOURCE);
        int choice = getArguments().getInt(Global.CHOICE_RESOURCE);
//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
//        imageView.setImageUrl(url, imageLoader);

//        GlideUrl uri = new GlideUrl(url, new LazyHeaders.Builder()
//                .setHeader("Session-Id", AppController.getInstance().getSessionId())
//                .build());
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView);

        for(int idBtn: CLICKABLE) {
            rootView.findViewById(idBtn).setOnClickListener(this);
            rootView.findViewById(idBtn).setOnLongClickListener(this);
        }


        botaoVerde = (Button)rootView.findViewById(R.id.botaoVerde);
        botaoAmarelo = (Button)rootView.findViewById(R.id.botaoAmarelo);
        botaoVermelho = (Button)rootView.findViewById(R.id.botaoVermelho);
        color_background = (ImageView)rootView.findViewById(R.id.color_background);


        editTextComment = (EditText)rootView.findViewById(R.id.editTextComment);
        layout_vote = rootView.findViewById(R.id.layout_vote);
        layout_container = rootView.findViewById(R.id.container);
        yourComment = (TextView)rootView.findViewById(R.id.yourComment);
        yourComment.setText(myComment);
        TextView textview = (TextView) rootView.findViewById(R.id.photo_description);
        textview.setText(text);
        TextView textViewVoted = (TextView) rootView.findViewById(R.id.photo_description_voted);
        textViewVoted.setText(text);
        textViewOwner = (TextView) rootView.findViewById(R.id.textViewOwner);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewOwner.setText(owner);
        textViewDate.setText(date);
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
        Log.d("myapp2", "**--page text  :" + text);
        state = new ListenerPagerStateChange() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //voltamos a mostrar as opções
                    Log.d("myapp2", "**--inboxpage  :" + state);
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
            Log.d("myapp", "showButtonsAndBackground");
            layout_container.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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

        }
        //Toast.makeText(v.getContext(), "Clicked Position: " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onLongClick(View v) {
        editTextComment.setVisibility(editTextComment.isShown()? View.GONE : View.VISIBLE);
        return true;
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

        botaoVermelho.setEnabled(false);
        botaoAmarelo.setEnabled(false);
        botaoVerde.setEnabled(false);
        final String comment = editTextComment.isShown()? editTextComment.getText().toString() : "";
        new SendVotePictureService(comment, id, vote, new CallbackMultiple<Boolean, String>() {
            @Override
            public void success(Boolean response) {

                if(getActivity() != null && getParentFragment() != null){
                    Toast.makeText(getActivity(), "Vote sent!", Toast.LENGTH_SHORT).show();
                    rootView.findViewById(R.id.layout_vote).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_already_voted).setVisibility(View.VISIBLE);
                    color_background.setVisibility(View.VISIBLE);
                    color_background.setBackgroundColor(getResources().getColor(getColor(vote)));

                    VerticalViewPager pager = ((InBoxFragment) getParentFragment()).getViewPager();
                    int nextPage = (pager.getCurrentItem() + 1) < pager.getAdapter().getCount()?  (pager.getCurrentItem() + 1) :  0;

                    if(nextPage > 0) {
                        animatePagerTransition(true, pager);
                        //pager.setCurrentItem(nextPage, true);
                    }

                    ((InBoxFragment)getParentFragment()).updateLocal(new PhotoInbox(id,comment,vote));
                }
            }

            @Override
            public void failed(String error) {
                toggleButtons(true);
                if(getActivity() != null)
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                if(getActivity() != null && getParentFragment() != null){
                    rootView.findViewById(R.id.layout_vote).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_already_voted).setVisibility(View.VISIBLE);
                    color_background.setVisibility(View.VISIBLE);
                    color_background.setBackgroundColor(getResources().getColor(getColor(vote)));

                    VerticalViewPager pager = ((InBoxFragment) getParentFragment()).getViewPager();
                    int nextPage = (pager.getCurrentItem() + 1) < pager.getAdapter().getCount()?  (pager.getCurrentItem() + 1) :  0;

                    if(nextPage > 0) {
                        animatePagerTransition(true, pager);
                        //pager.setCurrentItem(nextPage, true);

                    }
                }
                botaoVermelho.setEnabled(true);
                botaoAmarelo.setEnabled(true);
                botaoVerde.setEnabled(true);
            }
        }).execute();
    }

    private void toggleButtons(boolean enable){
        botaoVerde.setEnabled(enable);
        botaoAmarelo.setEnabled(enable);
        botaoVermelho.setEnabled(enable);
    }



}