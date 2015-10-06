package com.tappitz.tappitz;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.communication.RestClient;
import com.tappitz.tappitz.validators.EmailValidator;
import com.tappitz.tappitz.validators.PasswordValidator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import android.support.v4.app.Fragment;


public class LoginFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private final static int[] CLICKABLES = {R.id.loginBtn, R.id.registerBtn, R.id.backToPrevious, R.id.nextTo, R.id.pickdate};
    private final static int[] SCREENS = {R.id.screen_login, R.id.screen_reg1, R.id.screen_reg2, R.id.bottom_actions};
    private int curScreen;
    View rootView;
    AppCompatEditText UsrEmail, UsrPassword;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        for (int id: CLICKABLES)
            rootView.findViewById(id).setOnClickListener(this);
        UsrEmail = (AppCompatEditText) rootView.findViewById(R.id.loginEmail);
        UsrPassword = (AppCompatEditText) rootView.findViewById(R.id.loginPassword);
        curScreen = 0;

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        String user = prefs.getString(Global.KEY_USER, "");
        String pass = prefs.getString(Global.KEY_PASS, "");
        UsrEmail.setText(user);
        UsrPassword.setText(pass);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerBtn:
                Log.d("myapp", "****registerBtn**" + curScreen);
                curScreen = 1;
                showScreen(R.id.screen_reg1);

                break;
            case R.id.backToPrevious:
                Log.d("myapp", "****backToPrevious**" + curScreen);
                if(curScreen == 1)
                    showScreen(R.id.screen_login);
                else if(curScreen == 2)
                    showScreen(R.id.screen_reg1);
                curScreen--;
                break;
            case R.id.nextTo:
                Log.d("myapp", "****nextTo**" + curScreen);
                if(curScreen == 1){
                    curScreen = 2;
                    showScreen(R.id.screen_reg2);
                }

                //registar
                if(curScreen == 2){
                    ///
                }

                break;
            case R.id.pickdate:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                //dpd.show(getActivity().getSupportFragmentManager(), "Datepickerdialog");
                break;
            case R.id.loginBtn:
                Log.d("myapp", "****loginBtn**");
                if (validators()) {
                    Log.d("myapp", "****validators**");


                    RestClient.getService().login(UsrEmail.getText().toString(), UsrPassword.getText().toString(), new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            Log.d("myapp", "******" + jsonElement.toString());
                            Log.d("myapp", "***get(\"status\")***" + jsonElement.getAsJsonObject().get("status"));
                            onSuccessLogin();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("myapp", "**error****"+ error.toString());
                            onSuccessLogin();
                        }
                    });
                }


                break;
        }
    }

    private void onSuccessLogin(){

        //esconde o teclado caso estiver aberto
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        //guarda o mail e pass para nao ser preciso introduzi-los a toda a hora
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE); ;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Global.KEY_USER, UsrEmail.getText().toString());
        editor.putString(Global.KEY_PASS, UsrPassword.getText().toString());
        editor.apply();

        //abre o fragmento HOME
        ((MainActivity)getActivity()).displayView(Global.HOME);
    }

    private void showScreen(int screenId){
        Log.d("myapp", "****nextTo**" + curScreen);
        Log.d("myapp", "****screen_login**" + (screenId == R.id.screen_login));
        Log.d("myapp", "****screen_reg1**" + (screenId == R.id.screen_reg1));

        for(int id: SCREENS) {
            rootView.findViewById(id).setVisibility(id == screenId ? View.VISIBLE : View.GONE);
            Log.d("myapp", "****id == screenId**" + (id == screenId));
        }
        if(screenId != R.id.screen_login)
            rootView.findViewById(R.id.bottom_actions).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.d("myapp", date);
        //dateTextView.setText(date);
    }


    private boolean validators() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordValidator passwordValidator = new PasswordValidator();

        boolean emailValidated = emailValidator.validate(UsrEmail.getText().toString());
        boolean passwordValidated = passwordValidator.validate(UsrPassword.getText().toString());
        TextInputLayout textMsgWrapperUser = (TextInputLayout) rootView.findViewById(R.id.textMsgWrapperUser);
        TextInputLayout textMsgWrapperPass = (TextInputLayout) rootView.findViewById(R.id.textMsgWrapperPass);
        if(!emailValidated) {
            textMsgWrapperUser.setErrorEnabled(true);
            textMsgWrapperUser.setError("Email must ...");
        }else
            textMsgWrapperUser.setErrorEnabled(false);

        if(!passwordValidated){
            textMsgWrapperPass.setErrorEnabled(true);
            textMsgWrapperPass.setError("Password must ...");
        }else
            textMsgWrapperPass.setErrorEnabled(false);

        return emailValidated && passwordValidated;
    }
}
