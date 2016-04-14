package com.tappitz.app.ui.secondary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.ui.ScreenSlidePagerActivity;


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
                .into((ImageView) rootView.findViewById(R.id.picture));
        //refresh();

        rootView.findViewById(R.id.btn_go_inbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ContactContainer", "onclick");
                if(((ScreenSlidePagerActivity)getActivity()).getMiddleShowPage() != null){
                    ((ScreenSlidePagerActivity)getActivity()).getMiddleShowPage().showPage(Global.MIDDLE_BLANK);
                }
            }
        });


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
