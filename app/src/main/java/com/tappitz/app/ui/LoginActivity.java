package com.tappitz.app.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.JsonElement;
import com.tappitz.app.Global;
import com.tappitz.app.R;
import com.tappitz.app.rest.RestClient;
import com.tappitz.app.rest.RestClientV2;
import com.tappitz.app.rest.model.UserRegister;
import com.tappitz.app.rest.service.CallbackFromService;
import com.tappitz.app.rest.service.CallbackMultiple;
import com.tappitz.app.rest.service.LoginService;
import com.tappitz.app.rest.service.RegisterService;
import com.tappitz.app.ui.secondary.DatePickerFragment;
import com.tappitz.app.util.HashUtil;
import com.tappitz.app.util.ProgressGenerator;
import com.tappitz.app.validators.EmailValidator;
import com.tappitz.app.validators.NameValidator;
import com.tappitz.app.validators.PasswordValidator;
import com.tappitz.app.validators.PhoneValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends FragmentActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, ProgressGenerator.OnCompleteListener {


    private final static int[] CLICKABLES = {R.id.btn_login, R.id.link_signup, R.id.backToPrevious, R.id.nextTo, R.id.textViewCountry};
    private final static int[] SCREENS = {R.id.screen_login, R.id.screen_reg1, R.id.screen_reg2, R.id.bottom_actions};
    private int curScreen, day, month, year;
    AppCompatEditText editEmail, editPassword, date_picker, firstname, lastname, registerEmail, registerPassword, registerPhoneNumber, registerUsername;
    private AppCompatRadioButton female, male;
    private Spinner paisesSpinner;
    private ActionProcessButton login;
    private ProgressGenerator progressGenerator;
    private Handler handler;
    private Runnable runnable;
    private Button btn_date_picker;
    private TextView textViewDate, textViewError;

    private List<Integer> screens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        screens = new ArrayList<>();

        for (int id: CLICKABLES)
            findViewById(id).setOnClickListener(this);
        editEmail = (AppCompatEditText) findViewById(R.id.loginEmail);
        editPassword = (AppCompatEditText) findViewById(R.id.loginPassword);

        firstname = (AppCompatEditText) findViewById(R.id.firstname);
        lastname = (AppCompatEditText) findViewById(R.id.lastname);

        female = (AppCompatRadioButton) findViewById(R.id.female);
        male = (AppCompatRadioButton) findViewById(R.id.male);

        registerEmail = (AppCompatEditText) findViewById(R.id.registerEmail);
        registerPassword = (AppCompatEditText) findViewById(R.id.registerPassword);
        registerPhoneNumber = (AppCompatEditText) findViewById(R.id.registerPhoneNumber);
        registerUsername = (AppCompatEditText) findViewById(R.id.registerUsername);
        login = (ActionProcessButton)findViewById(R.id.btn_login);
        login.setMode(ActionProcessButton.Mode.ENDLESS);
        textViewError = (TextView)findViewById(R.id.textViewError);
        curScreen = R.id.screen_login;

        SharedPreferences prefs = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
        String user = prefs.getString(Global.KEY_USER, "");
//        String pass = prefs.getString(Global.KEY_PASS, "");
        editEmail.setText(user);
//        editPassword.setText(pass);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                login.setProgress(0);
            }
        };

//        textViewDate = (TextView)findViewById(R.id.textViewDate);
//        btn_date_picker = (Button)findViewById(R.id.btn_date_picker);
//        btn_date_picker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchCalendar();
//            }
//        });
        date_picker = (AppCompatEditText)findViewById(R.id.date_picker);
        date_picker.setOnClickListener(this);
        ((TextInputLayout)findViewById(R.id.date_holder)).setOnClickListener(this);
//        date_picker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchCalendar();
//            }
//        });





        //definicao dos spinner para os paises
        paisesSpinner = (Spinner) findViewById(R.id.counties);
        String[] paises = getResources().getStringArray(R.array.paises);
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paises);
        paisesSpinner.setAdapter(adapterC);

        int spinnerPosition = adapterC.getPosition(Locale.getDefault().getDisplayCountry());
        if(spinnerPosition >= 0){
            paisesSpinner.setSelection(spinnerPosition);
        }

        progressGenerator = new ProgressGenerator(this);
//        final ActionProcessButton btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
//        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);

    }
    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.link_signup:
                Log.d("myapp", "****registerBtn**" + curScreen);
                screens.add(R.id.screen_login);
                showScreen(R.id.screen_reg1);

                String email = getPhoneEmail();
                registerEmail.setText(email);
//                registerPassword.setText(editPassword.getText().toString());

                break;
            case R.id.backToPrevious:
                Log.d("myapp", "****backToPrevious**" + curScreen);
                onBackPressed();
                break;
            case R.id.nextTo:
                Log.d("myapp", "****nextTo**" + curScreen);
                if(curScreen == R.id.screen_reg1){
                    if(!validateScreen(curScreen))
                        return;
                    screens.add(R.id.screen_reg1);
                    showScreen(R.id.screen_reg2);
                } else if(curScreen == R.id.screen_reg2){
                    //registar
                    if(!validateScreen(curScreen))
                        return;
                    registerUser();
                }

                break;
            case R.id.btn_login:
                Log.d("myapp", "****loginBtn**");
                if (validators()) {
                    login(editEmail.getText().toString(), HashUtil.computeSHAHash(editPassword.getText().toString()));
                }
                break;
            case R.id.date_picker:
                launchCalendar();
                break;
            case R.id.date_holder:
                launchCalendar();
                break;
            case R.id.textViewCountry:
                paisesSpinner.performClick();
                break;
        }
    }

    private void login(String email, String password){

            Log.d("myapp", "****validators**");

            login.setEnabled(false);
            editEmail.setEnabled(false);
            editPassword.setEnabled(false);
            //progressGenerator.start(login);
            login.setProgress(50);
            final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "You're about to experience the tAPPitz effect!", true);

            new LoginService(email, password, new CallbackMultiple<String, String>() {
                @Override
                public void success(String sessionId) {

                    if(sessionId.length() > 0){
                        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);
//                                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("sessionId", sessionId);
                        editor.commit();
                        Log.d("myapp", "***login**sessionId*" + sessionId);
                        if(Global.VERSION_V2) {
                            RestClientV2.setSessionId(sessionId);
                        }else {
                            RestClient.setSessionId(sessionId);
                        }
//                        RestClient.setSessionId(sessionId);
                        login.setProgress(100);
                        onSuccessLogin();
                    }
                }

                @Override
                public void failed(String error) {

                    login.setEnabled(true);
                    editEmail.setEnabled(true);
                    editPassword.setEnabled(true);
                    progressDialog.dismiss();
                    login.setProgress(-1);
                    Log.d("myapp", "***login**error*" + error);
                    editEmail.setError(error);
                    new Handler().postDelayed(runnable, 2000);
                    //onSuccessLogin();
                }
            }).execute();

    }


    private void registerUser(){

        final String firstName, lastName, gender, birthDate, phoneNumber, country, gpsCoordinates, email, password, username;

        firstName = firstname.getText().toString().trim();
        lastName = lastname.getText().toString().trim();
        gender = male.isChecked()? "1": "0";
        //gender = Boolean.toString(male.isChecked());
        birthDate = day + "_" + month + "_" + year;
        phoneNumber = registerPhoneNumber.getText().toString();
        Log.d("myapp", "***phoneNumber.length()***" + phoneNumber.length());
        country = paisesSpinner.getSelectedItemPosition() + "";
        gpsCoordinates = "1234";
        email = registerEmail.getText().toString().toLowerCase().trim();
        password = HashUtil.computeSHAHash(registerPassword.getText().toString());
        username = registerUsername.getText().toString().replace(" ","");
        UserRegister user = new UserRegister(firstName, lastName, gender, birthDate, phoneNumber, country, gpsCoordinates, email, password, username);


        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "You're about to experience the tAPPitz effect!", true);

        new RegisterService(user, new CallbackFromService() {
            @Override
            public void success(Object response) {
                JsonElement json = (JsonElement) response;
                Log.d("myapp", "***get(status)***" + json.getAsJsonObject().get("status"));
                String status = json.getAsJsonObject().get("status").toString();
                if(status.equals("true")){
                    Log.d("myapp", "***get(status)**true*");
                    editEmail.setText(registerEmail.getText().toString());
//                    editPassword.setText(registerPassword.getText().toString());
                    screens.add(R.id.screen_reg2);
                    showScreen(R.id.screen_login);

                    login(email, password);
                }else{
                    Log.d("myapp", "***get(status)**false*");
                    String error = json.getAsJsonObject().get("error").toString();
                    editPassword.setError(error);
                    textViewError.setText(error);
                    textViewError.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();


            }

            @Override
            public void failed(Object error) {
                Toast.makeText(getApplicationContext(), "There was an eror, Try again later.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).execute();


    }


    @Override
    public void onBackPressed() {

        if(screens != null && screens.size() > 0){
            showScreen(screens.remove(screens.size() - 1));
            return;
        }

        super.onBackPressed();
    }

    private void launchCalendar(){
        Log.d("myapp", "***launchCalendar*");
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
        newFragment.setDate(year, month, day);
        newFragment.setDatePicked(new DatePickerFragment.DatePicked() {
            @Override
            public void onDatePicked(int year1, int monthOfYear, int dayOfMonth) {
                Log.d("myapp", "***launchCalendar*" + dayOfMonth+"-"+(monthOfYear+1)+"-"+year1);
                //textViewDate.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year1);
                date_picker.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year1);
                day = dayOfMonth;
                month = (monthOfYear+1);
                year = year1;
            }
        });


//        Calendar now = Calendar.getInstance();
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                this,
//                (year > 0)? year : now.get(Calendar.YEAR),
//                (month > 0)? month : now.get(Calendar.MONTH),
//                (day > 0)? day : now.get(Calendar.DAY_OF_MONTH)
//        );
////        dpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
//        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void onSuccessLogin(){

        //esconde o teclado caso estiver aberto
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //guarda o mail e pass para nao ser preciso introduzi-los a toda a hora
        SharedPreferences prefs = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Global.KEY_USER, editEmail.getText().toString());
//        editor.putString(Global.KEY_PASS, editPassword.getText().toString());
        editor.apply();

        //abre o fragmento HOME
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 0);
        finish();
    }

    private boolean validateScreen(int screenId){
        if(screenId == R.id.screen_reg1){
            NameValidator nameValidator = new NameValidator();

            TextInputLayout firstname_holder = (TextInputLayout)findViewById(R.id.textMsgWrapperfirstname);
            TextInputLayout lastname_holder = (TextInputLayout)findViewById(R.id.textMsgWrapperlastname);
            TextInputLayout date_holder = (TextInputLayout)findViewById(R.id.date_holder);

            boolean nameFirstValidated = nameValidator.validate(firstname.getText().toString());
            boolean nameLastValidated = nameValidator.validate(lastname.getText().toString());
            boolean radioSelected = male.isChecked() || female.isChecked();
            Log.d("myapp", "***day*" + day);
            Log.d("myapp", "***month*" + month);
            Log.d("myapp", "***year*" + year);
            boolean dateSelected = year > 0 && month > 0 && day > 0;

            if(nameFirstValidated){
                firstname_holder.setErrorEnabled(false);
            }else{
                firstname_holder.setErrorEnabled(true);
                firstname_holder.setError("Required");
            }

            if(nameLastValidated){
                lastname_holder.setErrorEnabled(false);
            }else{
                lastname_holder.setErrorEnabled(true);
                lastname_holder.setError("Required");
            }

            if(radioSelected){
                male.setError(null);
            }else{
                male.setError("Required");
            }

            if(dateSelected){
                date_holder.setErrorEnabled(false);
            }else{
                date_holder.setErrorEnabled(true);
                date_holder.setError("Required");
            }

            return nameFirstValidated && nameLastValidated && radioSelected && dateSelected;

        }else if(screenId == R.id.screen_reg2){
            EmailValidator emailValidator = new EmailValidator();
            PasswordValidator passwordValidator = new PasswordValidator();
            PhoneValidator phoneValidator = new PhoneValidator();

            TextInputLayout email_holder = (TextInputLayout)findViewById(R.id.email_holder);
            TextInputLayout pass_holder = (TextInputLayout)findViewById(R.id.pass_holder);
            TextInputLayout phone_holder = (TextInputLayout)findViewById(R.id.phone_holder);
            TextInputLayout username_holder = (TextInputLayout)findViewById(R.id.username_holder);

            boolean emailValidated = emailValidator.validate(registerEmail.getText().toString());
            boolean passValidated = passwordValidator.validate(registerPassword.getText().toString());
            boolean phoneValidated = true;
            boolean usernameValidated = !isEmpty(registerUsername);
//            boolean phoneValidated = phoneValidator.validate(registerPhoneNumber.getText().toString());

            if(!emailValidated) {
                email_holder.setErrorEnabled(true);
                email_holder.setError("Required");
            }else
                email_holder.setErrorEnabled(false);

            if(!passValidated) {
                pass_holder.setErrorEnabled(true);
                pass_holder.setError("Required");
            }else
                pass_holder.setErrorEnabled(false);

            if(!usernameValidated){
                username_holder.setErrorEnabled(true);
                username_holder.setError("Required");
            }else{
                username_holder.setErrorEnabled(false);
            }

//            if(!phoneValidated) {
//                phone_holder.setErrorEnabled(true);
//                phone_holder.setError("Required");
//            }else
//                phone_holder.setErrorEnabled(false);

            return emailValidated && passValidated && phoneValidated && usernameValidated;
        }
        return false;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void showScreen(int screenId){

        curScreen = screenId;
        for(int id: SCREENS) {
            findViewById(id).setVisibility(id == screenId ? View.VISIBLE : View.GONE);
        }
        if(screenId != R.id.screen_login)
           findViewById(R.id.bottom_actions).setVisibility(View.VISIBLE);

        if(screenId == R.id.screen_reg2){
            ((Button)findViewById(R.id.nextTo)).setText("Sign Up!");
        }else{
            ((Button)findViewById(R.id.nextTo)).setText("Next");
        }
    }

//    @Override
//    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//        date_picker.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
//        this.day = dayOfMonth;
//        this.month = monthOfYear;
//        this.year = year;
//    }


    private boolean validators() {
        PasswordValidator passwordValidator = new PasswordValidator();

        boolean emailValidated = !editEmail.getText().toString().trim().isEmpty();
        boolean passwordValidated = passwordValidator.validate(editPassword.getText().toString());

        if(!emailValidated) {
            editEmail.setError("Email must ...");
        }else
            editEmail.setError(null);

        if(!passwordValidated){
            editPassword.setError("Password must ...");
        }else
            editPassword.setError(null);

        return emailValidated && passwordValidated;
    }

    @Override
    public void onComplete() {

    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        date_picker.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
        this.day = dayOfMonth;
        this.month = monthOfYear;
        this.year = year;
    }

    private String getPhoneEmail(){
        Account[] accountList = AccountManager.get(this).getAccountsByType("com.google");
        String email = "" ;
        if(accountList != null && accountList.length > 0)
            email = accountList[0].name;
        Log.d("Play store account:" , email);
        return email;
    }
}
