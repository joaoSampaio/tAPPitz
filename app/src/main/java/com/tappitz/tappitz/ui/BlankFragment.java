package com.tappitz.tappitz.ui;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.tappitz.tappitz.R;


public class BlankFragment extends Fragment  {

    final static int[] CLICABLES = {R.id.logOut, R.id.camera_options, R.id.go_to, R.id.btn_goto_in, R.id.btn_goto_out, R.id.btn_goto_friends, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btn_shutter};
    View rootView, camera_options;
    private Button btn_shutter;
    RelativeLayout whiteBackground;
    private View textMsgWrapper;
    private EditText textMsg;


    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_blank, container, false);




        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view;
                switch (v.getId()) {
                    case R.id.camera_options:
                        v.setTag(null);
                        view = rootView.findViewById(R.id.layout_camera);
                        view.setVisibility(view.isShown() ? View.GONE : View.VISIBLE);

                        rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
                        break;
                    case R.id.go_to:
                        view = rootView.findViewById(R.id.layout_goto);
                        view.setVisibility(view.isShown() ? View.GONE : View.VISIBLE);

                        rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);
                        break;
                    case R.id.logOut:
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 0);
                        getActivity().finish();
                        return;


//                    case R.id.btn_shutter:
//
//                        //rootView.bringToFront();
//
//                        //onTakePick(true);
//                        break;
//
//                    case R.id.btnPhotoDelete:
//
//                        textMsgWrapper.setVisibility(View.INVISIBLE);
//                        onTakePick(false);
//                        textMsg.setText("");
//                        break;
////                    case R.id.btnText:
////                        showEditText();
////
////                        break;
//                    case R.id.btnPhotoAccept:
//                        v.setTag(textMsg.getText().toString());
//                        break;
                }
                ((ScreenSlidePagerActivity)getActivity()).pass(v);
            }
        };

        camera_options = rootView.findViewById(R.id.camera_options);
        btn_shutter = (Button) rootView.findViewById(R.id.btn_shutter);
        textMsgWrapper = rootView.findViewById(R.id.textMsgWrapper);
        whiteBackground = (RelativeLayout)rootView.findViewById(R.id.whiteBackground);
        textMsg = (EditText)rootView.findViewById(R.id.textMsg);

        whiteBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("myapp", "setOnTouchListener blank ");
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        Log.d("myapp", "inbox ACTION_DOWN");
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("myapp", "inbox ACTION_POINTER_DOWN");
                        //=====Write down your Finger Pressed code here

                        camera_options.setTag("hide");
                        ((ScreenSlidePagerActivity) getActivity()).pass(camera_options);


                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d("myapp", "inbox ACTION_UP");
                    case MotionEvent.ACTION_POINTER_UP:
                        //=====Write down you code Finger Released code here
                    case MotionEvent.ACTION_MOVE:
                        Log.d("myapp", "inbox ACTION_MOVE");

                }

                return false;
            }
        });



        for(int id : CLICABLES)
                rootView.findViewById(id).setOnClickListener(click);



        //rootView.getParent().requestDisallowInterceptTouchEvent(false);
//        rootView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("myapp2", "blank");
//                //getActivity().onTouchEvent(event);
//                ((ScreenSlidePagerActivity)getActivity()).pass(event);
//                return false;
//            }
//        });
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("myapp", "onResume");
        setUP();

    }

    @Override
    public void onStop(){
        super.onStop();
        ((ScreenSlidePagerActivity)getActivity()).setListenerCamera(null);
    }

    private void setUP(){

        Log.d("myapp", "setUP blank ");
        textMsgWrapper.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.VISIBLE);

        showBtnOptions(false);
        btn_shutter.setVisibility(View.GONE);
        ((ScreenSlidePagerActivity)getActivity()).setListenerCamera(new ScreenSlidePagerActivity.HomeToBlankListener() {
            @Override
            public void onCameraAvailable() {
                Log.d("myapp", "onCameraAvailable blank ");
                btn_shutter.setVisibility(View.VISIBLE);
                showBtnOptions(true);
            }


        });

        if(isCameraInUse()){
            Log.d("myapp", "isCameraInUse blank ");
            btn_shutter.setVisibility(View.VISIBLE);
            showBtnOptions(true);
        }
    }

    public boolean isCameraInUse() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (c != null) c.release();
        }
        return false;
    }

//    private void onCameraAvailable(){
//
//        btn_shutter.setVisibility(View.VISIBLE);
//        showBtnOptions(true);
//    }



    private void showBtnOptions(boolean show){
        rootView.findViewById(R.id.camera_options).setVisibility(!show ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);
        rootView.findViewById(R.id.go_to).setVisibility(!show ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.layout_goto).setVisibility(View.GONE);
    }

//    private void onTakePick(final boolean takePhoto)
//    {
//        //este metodo esconde o menu da camera ou mostra o botao para tirar foto, simplesmente tem animações porque era codigo que ja tinha feito para outra app
//
//        btn_shutter.setVisibility(takePhoto ? View.GONE : View.VISIBLE);
//        showBtnOptions(!takePhoto);
//        whiteBackground.setVisibility(takePhoto ? View.VISIBLE : View.GONE);
//    }
//
//    private void showEditText(){
//        Log.d("myapp", "showEditText");
//        final boolean isVisible = textMsgWrapper.isShown();
//        rootView.findViewById( R.id.btnText).setEnabled(false);
//        textMsgWrapper.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
//        rootView.findViewById(R.id.btnText).setEnabled(true);
//
//        if(!isVisible){
//            textMsg.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(textMsg, InputMethodManager.SHOW_IMPLICIT);
//        }else {
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
//        }
//    }


}
