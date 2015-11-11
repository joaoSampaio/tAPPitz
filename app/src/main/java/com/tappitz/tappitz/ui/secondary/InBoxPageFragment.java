package com.tappitz.tappitz.ui.secondary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.SendVotePictureService;
import com.tappitz.tappitz.ui.InBoxFragment;
import com.tappitz.tappitz.util.VerticalViewPager;


public class InBoxPageFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private View rootView, layout_vote, layout_container;
    private TextView textViewOwner, textViewDate;
    private Button botaoVermelho, botaoAmarelo, botaoVerde;
    private ImageView color_background;
    private EditText editTextComment;
    private int id;


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
        id = getArguments().getInt(Global.ID_RESOURCE);
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
            rootView.findViewById(idBtn).setOnLongClickListener(this);
        }


        botaoVerde = (Button)rootView.findViewById(R.id.botaoVerde);
        botaoAmarelo = (Button)rootView.findViewById(R.id.botaoAmarelo);
        botaoVermelho = (Button)rootView.findViewById(R.id.botaoVermelho);
        color_background = (ImageView)rootView.findViewById(R.id.color_background);


        editTextComment = (EditText)rootView.findViewById(R.id.editTextComment);
        layout_vote = rootView.findViewById(R.id.layout_vote);
        layout_container = rootView.findViewById(R.id.container);

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
                        layout_container.setVisibility(View.INVISIBLE);
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp", "inbox ACTION_UP");
                        layout_container.setVisibility(View.VISIBLE);
                    case MotionEvent.ACTION_POINTER_UP:
                        //=====Write down you code Finger Released code here
                    case MotionEvent.ACTION_MOVE:
                        Log.d("myapp", "inbox ACTION_MOVE");
                        layout_container.setVisibility(View.VISIBLE);

                }

                return false;
            }
        });

        return rootView;
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
        editTextComment.setVisibility(editTextComment.isShown()? View.INVISIBLE : View.VISIBLE);
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
        Toast.makeText(getActivity(), "Vote sent!", Toast.LENGTH_SHORT).show();
        String comment = editTextComment.isShown()? editTextComment.getText().toString() : "";
        new SendVotePictureService(comment, id, vote, new CallbackMultiple() {
            @Override
            public void success(Object response) {

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
            }

            @Override
            public void failed(Object error) {
                toggleButtons(true);



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
            }
        }).execute();
    }

    private void toggleButtons(boolean enable){
        botaoVerde.setEnabled(enable);
        botaoAmarelo.setEnabled(enable);
        botaoVermelho.setEnabled(enable);
    }



}