package com.tappitz.app.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.app.AppController;
import com.tappitz.app.model.Contact;
import com.tappitz.app.rest.model.ContactSendId;
import com.tappitz.app.rest.service.CallbackMultiple;
import com.tappitz.app.rest.service.OperationContactService;
import com.tappitz.app.rest.service.SearchContactService;


public class AddContactsDialogFragment extends DialogFragment {

    private View rootView;


    private TextWatcher mSearchTw;
    public EditText mSearchEdt;
    private TextView mName, mUsername, mCircle, text_no_contact;
    private Button action_follow;
    private View container, progress, progressOperation;
    private Handler handler;
    public AddContactsDialogFragment() {
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
public static AddContactsDialogFragment newInstance() {
    AddContactsDialogFragment fragment = new AddContactsDialogFragment();
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
        rootView = inflater.inflate(R.layout.add_contacts_fragment, container, false);

        loadUI();
        //refresh();
        return rootView;
    }


    public void loadUI(){

        mName = (TextView) rootView.findViewById(R.id.CONTACT_name);
        mUsername = (TextView) rootView.findViewById(R.id.CONTACT_username);
        mCircle = (TextView)rootView.findViewById(R.id.CONTACT_circle);
        container = rootView.findViewById(R.id.container);
        progress = rootView.findViewById(R.id.progress);
        progressOperation  = rootView.findViewById(R.id.progressOperation);
        action_follow = (Button)rootView.findViewById(R.id.action_follow);
        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);
        container.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        text_no_contact.setVisibility(View.GONE);

        handler = new Handler();
        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewDescription.setText("Follow someone");

        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });



        mSearchEdt = (EditText)rootView.findViewById(R.id.mSearchEdt);


        mSearchTw=new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final String search = new String(s.toString());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSearchEdt.getText().toString().equals(search.toString())) {
                            Log.d("ADDContact", "text changed is the same as 1s ago:"+mSearchEdt.getText().toString() + "||||"+ search.toString());
                            searchContact();
                        } else {
                            Log.d("ADDContact", "text changed in less than 1s");
                        }
                    }
                }, 1500);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mSearchEdt.addTextChangedListener(mSearchTw);
        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // showToast("Pedido ao servidor");
                    //Toast.makeText(getActivity(), "Pedido ao servidor", Toast.LENGTH_SHORT).show();
                    searchContact();
                    return true;
                }
                return false;
            }
        });


        text_no_contact = (TextView)rootView.findViewById(R.id.text_no_contact);



    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void searchContact(){
        String searchParam = mSearchEdt.getText().toString();

        progress.setVisibility(View.VISIBLE);
        new SearchContactService(searchParam, new CallbackMultiple<Contact, String>() {
            @Override
            public void success(final Contact contact) {

                if(getActivity() != null && contact != null) {
                    progress.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);
                    text_no_contact.setVisibility(View.GONE);
                    contact.setIsFollower(false);
                    contact.setIsFriend(false);
                    mName.setText(contact.getName());
                    mUsername.setText(contact.getUsername());

                    mCircle.setText(contact.getLetters());

                    GradientDrawable bgShape = (GradientDrawable) mCircle.getBackground();
                    bgShape.setColor(Color.parseColor("#33b5e5"));

                    action_follow.setVisibility(View.VISIBLE);
                    action_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressOperation.setVisibility(View.VISIBLE);
                            action_follow.setVisibility(View.GONE);
                            new OperationContactService(new ContactSendId(contact.getId(), Global.OPERATION_TYPE_INVITE), new CallbackMultiple<Boolean, String>() {
                                @Override
                                public void success(Boolean response) {
                                    if(getActivity() != null)
                                        progressOperation.setVisibility(View.GONE);

                                    Toast.makeText(AppController.getAppContext(), "Follow successful", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void failed(String error) {
                                    if(getActivity() != null)
                                        progressOperation.setVisibility(View.GONE);
                                    Toast.makeText(AppController.getAppContext(), " "+ error, Toast.LENGTH_LONG).show();
                                }
                            }).execute();
                        }
                    });

                }


            }

            @Override
            public void failed(String error) {
                if(getActivity() != null) {
                    progress.setVisibility(View.GONE);
                    text_no_contact.setVisibility(View.VISIBLE);
                    container.setVisibility(View.GONE);

                }
            }
        }).execute();




//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

    }






}
