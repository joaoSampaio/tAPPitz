package com.tappitz.app.ui;

import android.content.Intent;
import android.graphics.Rect;
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

import com.tappitz.app.R;
import com.tappitz.app.app.AppController;

import java.util.ArrayList;
import java.util.List;


public class BlankFragment extends Fragment  {

    final static int[] CLICABLES = {R.id.logOut, R.id.camera_options, R.id.btn_load, R.id.btn_flash, R.id.btn_toggle_camera, R.id.btn_shutter,
            R.id.action_goto_sent, R.id.action_goto_received, R.id.action_goto_qrcode, R.id.action_goto_contacts, R.id.inbox_circle, R.id.outbox_circle};

    View rootView, camera_options, layout_before_photo;
    private Button btn_shutter;
    private EditText textMsg;
    private View.OnClickListener click;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        ((MainActivity)getActivity()).setButtonEnable(new ButtonEnable() {
            @Override
            public void enableCameraButtons(boolean enable) {

                rootView.findViewById(R.id.btn_toggle_camera).setEnabled(enable);
                rootView.findViewById(R.id.btn_flash).setEnabled(enable);
                rootView.findViewById(R.id.btn_load).setEnabled(enable);
                rootView.findViewById(R.id.camera_options).setVisibility(View.VISIBLE);


            }
        });





        rootView.findViewById(R.id.btn_shutter).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                ((MainActivity)getActivity()).getmHelper().onLongClick(view);
                return true;
            }
        });


        click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view;
                Log.d("myapp", "click:" + v.getId());
                switch (v.getId()) {
                    case R.id.camera_options:
                        v.setTag(null);
                        view = rootView.findViewById(R.id.layout_camera);
                        view.setVisibility(view.isShown() ? View.GONE : View.VISIBLE);
                        break;
                    case R.id.logOut:
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 0);
                        getActivity().finish();
                        return;
                }
                ((MainActivity)getActivity()).pass(v);
            }
        };
        View layout_after_photo = rootView.findViewById(R.id.layout_after_photo);
        layout_after_photo.setVisibility(View.GONE);
        camera_options = rootView.findViewById(R.id.camera_options);
        btn_shutter = (Button) rootView.findViewById(R.id.btn_shutter);
        layout_before_photo = (RelativeLayout)rootView.findViewById(R.id.layout_before_photo);
        textMsg = (EditText)rootView.findViewById(R.id.textMsg);

        for(int id : CLICABLES)
                rootView.findViewById(id).setOnClickListener(click);

        return rootView;
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            Log.d("myapp", "autoFocus autoFocus myAutoFocusCallback:" + arg0);
        }};


int FOCUS_AREA_SIZE = 100;
    private Rect calculateFocusArea(float x, float y, float touchMajor, float touchMinor) {

        Rect tfocusRect = new Rect(
                (int)(x - touchMajor/2),
                (int)(y - touchMinor/2),
                (int)(x + touchMajor/2),
                (int)(y + touchMinor/2));

        final Rect targetFocusRect = new Rect(
                tfocusRect.left * 2000/layout_before_photo.getWidth() - 1000,
                tfocusRect.top * 2000/layout_before_photo.getHeight() - 1000,
                tfocusRect.right * 2000/layout_before_photo.getWidth() - 1000,
                tfocusRect.bottom * 2000/layout_before_photo.getHeight() - 1000);


//        int left = clamp(Float.valueOf((x / layout_after_photo.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
//        int top = clamp(Float.valueOf((y / layout_after_photo.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
//
//        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
        return targetFocusRect;
    }


    @Override
    public void onResume() {
        super.onResume();
        setUP();
    }

    @Override
    public void onStop(){
        super.onStop();
        ((MainActivity)getActivity()).setListenerCamera(null);
    }

    private void setUP(){

        Log.d("myapp", "setUP blank ");

        showBtnOptions(false);
        btn_shutter.setVisibility(View.GONE);
        ((MainActivity)getActivity()).setListenerCamera(new MainActivity.HomeToBlankListener() {
            @Override
            public void onCameraAvailable() {
                Log.d("myapp", "onCameraAvailable blank ");
                btn_shutter.setVisibility(View.VISIBLE);
                showBtnOptions(true);
            }


        });

        if(isCameraInUse()){

            btn_shutter.setVisibility(View.VISIBLE);
            showBtnOptions(true);
            Log.d("myapp", "isCameraInUse blank " + rootView.findViewById(R.id.camera_options).isShown());
        }
    }

    public boolean isCameraInUse() {
        return AppController.getInstance().mCameraReady;
    }


    private void enableFocus(){
        layout_before_photo.setOnTouchListener(new View.OnTouchListener() {
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
                        ((MainActivity) getActivity()).pass(camera_options);


                        Camera camera = AppController.getInstance().mCamera;
                        if (camera != null) {
                            camera.cancelAutoFocus();
                            //Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
                            Rect focusRect = calculateFocusArea(event.getX(), event.getY(), event.getTouchMajor(), event.getTouchMinor());
                            Camera.Parameters parameters = camera.getParameters();
                            if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                            }
                            if (parameters.getMaxNumFocusAreas() > 0) {
                                List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                                mylist.add(new Camera.Area(focusRect, 1000));
                                parameters.setFocusAreas(mylist);
                            }

                            try {
                                camera.cancelAutoFocus();
                                camera.setParameters(parameters);
                                camera.startPreview();
                                camera.autoFocus(new Camera.AutoFocusCallback() {
                                    @Override
                                    public void onAutoFocus(boolean success, Camera camera) {
                                        if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                                            Camera.Parameters parameters = camera.getParameters();
                                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                            if (parameters.getMaxNumFocusAreas() > 0) {
                                                parameters.setFocusAreas(null);
                                            }
                                            camera.setParameters(parameters);
                                            camera.startPreview();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }



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
    }


    private void showBtnOptions(boolean show){
        rootView.findViewById(R.id.camera_options).setVisibility(!show ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.layout_camera).setVisibility(View.GONE);


    }

    public interface ButtonEnable{
        void enableCameraButtons(boolean enable);
    }

}
