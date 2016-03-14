package com.tappitz.tappitz.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.app.AppController;
import com.tappitz.tappitz.model.Contact;
import com.tappitz.tappitz.rest.service.CallbackMultiple;
import com.tappitz.tappitz.rest.service.SearchContactService;


public class QRCodeFragment extends Fragment {

    private View rootView;



    public QRCodeFragment() {
        // Required empty public constructor
    }



//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//
//     * @return A new instance of fragment ContactsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CustomDialogFragment newInstance() {
//        CustomDialogFragment fragment = new CustomDialogFragment();
//        return fragment;
//    }
public static QRCodeFragment newInstance() {
    QRCodeFragment fragment = new QRCodeFragment();
    return fragment;
}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qr_code, container, false);

        Glide.with(this)
                .load(R.drawable.transparent_tappitz)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .override(AppController.getInstance().width, AppController.getInstance().height)
                .into((ImageView) rootView.findViewById(R.id.picture));
        //refresh();
        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();
           }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
