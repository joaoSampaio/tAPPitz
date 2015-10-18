package com.tappitz.tappitz.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.model.UserRegister;
import com.tappitz.tappitz.rest.service.CallbackFromService;
import com.tappitz.tappitz.rest.service.LoginService;
import com.tappitz.tappitz.rest.service.RegisterService;
import com.tappitz.tappitz.validators.EmailValidator;
import com.tappitz.tappitz.validators.NameValidator;
import com.tappitz.tappitz.validators.PasswordValidator;
import com.tappitz.tappitz.validators.PhoneValidator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;

public class LoginActivity extends Activity  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {


    private final static int[] CLICKABLES = {R.id.btn_login, R.id.link_signup, R.id.backToPrevious, R.id.nextTo, R.id.offline, R.id.offlineTabs};
    private final static int[] SCREENS = {R.id.screen_login, R.id.screen_reg1, R.id.screen_reg2, R.id.bottom_actions};
    private int curScreen, day, month, year;
    AppCompatEditText UsrEmail, UsrPassword, date_picker, firstname, lastname, registerEmail, registerPassword, registerPhoneNumber;
    private AppCompatRadioButton female, male;
    private Spinner paisesSpinner;
    private Button login;

    private List<Integer> screens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        screens = new ArrayList<>();

        for (int id: CLICKABLES)
            findViewById(id).setOnClickListener(this);
        UsrEmail = (AppCompatEditText) findViewById(R.id.loginEmail);
        UsrPassword = (AppCompatEditText) findViewById(R.id.loginPassword);

        firstname = (AppCompatEditText) findViewById(R.id.firstname);
        lastname = (AppCompatEditText) findViewById(R.id.lastname);

        female = (AppCompatRadioButton) findViewById(R.id.female);
        male = (AppCompatRadioButton) findViewById(R.id.male);

        registerEmail = (AppCompatEditText) findViewById(R.id.registerEmail);
        registerPassword = (AppCompatEditText) findViewById(R.id.registerPassword);
        registerPhoneNumber = (AppCompatEditText) findViewById(R.id.registerPhoneNumber);

        login = (Button)findViewById(R.id.btn_login);
        curScreen = R.id.screen_login;

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String user = prefs.getString(Global.KEY_USER, "");
        String pass = prefs.getString(Global.KEY_PASS, "");
        UsrEmail.setText(user);
        UsrPassword.setText(pass);



        date_picker = (AppCompatEditText)findViewById(R.id.date_picker);
        date_picker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.clearFocus();
                    launchCalendar();
                } else {

                }
            }
        });

        //definicao dos spinner para os paises
        paisesSpinner = (Spinner) findViewById(R.id.counties);
        String[] paises = getResources().getStringArray(R.array.paises);
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paises);
        paisesSpinner.setAdapter(adapterC);

        int spinnerPosition = adapterC.getPosition(Locale.getDefault().getDisplayCountry());
        if(spinnerPosition >= 0){
            paisesSpinner.setSelection(spinnerPosition);
        }




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
            case R.id.offline:
                onSuccessLogin();
                break;
            case R.id.offlineTabs:
                Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
                startActivityForResult(intent, 0);
                finish();
                break;
            case R.id.link_signup:
                Log.d("myapp", "****registerBtn**" + curScreen);
                screens.add(R.id.screen_login);
                showScreen(R.id.screen_reg1);


                registerEmail.setText(UsrEmail.getText().toString());
                registerPassword.setText(UsrPassword.getText().toString());

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
                    Log.d("myapp", "****validators**");

                    login.setEnabled(false);

                    final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "You're about to experience the tAPPitz effect!", true);



                    new LoginService(UsrEmail.getText().toString(), UsrPassword.getText().toString(), new CallbackFromService() {
                        @Override
                        public void success(Object response) {
                            if (response != null && response instanceof JsonElement) {
                                JsonElement r = (JsonElement)response;
                                Log.d("myapp", "***get(status)***" + r.getAsJsonObject().get("status"));
                                String status = r.getAsJsonObject().get("status").toString();
                                TextInputLayout textMsgWrapperUser = (TextInputLayout)findViewById(R.id.textMsgWrapperUser);
                                if(status.equals("true")){
                                    Log.d("myapp", "***get(status)**true*" );
                                    textMsgWrapperUser.setErrorEnabled(false);
                                    onSuccessLogin();
                                }else{
                                    Log.d("myapp", "***get(status)**false*" );
                                    textMsgWrapperUser.setErrorEnabled(true);
                                    String error = r.getAsJsonObject().get("error").toString();
                                    textMsgWrapperUser.setError(error);
                                }


                                Log.d("myapp", "******" + r.toString());
                                Log.d("myapp", "***get(\"status\")***" + r.getAsJsonObject().get("status"));

                            }
                            login.setEnabled(true);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void failed(Object error) {
                            if (error != null && error instanceof RetrofitError) {
                                //onSuccessLogin();
                                login.setEnabled(true);
                                progressDialog.dismiss();
                            }
                        }
                    }).execute();
                }
                break;
        }
    }


    private void registerUser(){

        String firstName, lastName, gender, birthDate, phoneNumber, country, gpsCoordinates, email, password;

        firstName = firstname.getText().toString();
        lastName = lastname.getText().toString();
        lastName = lastname.getText().toString();
        gender = Boolean.toString(male.isChecked());
        birthDate = day + "_" + month + "_" + year;
        phoneNumber = registerPhoneNumber.getText().toString();
        country = paisesSpinner.getSelectedItemPosition() + "";
        gpsCoordinates = "1234";
        email = registerEmail.getText().toString();
        password = registerPassword.getText().toString();
        UserRegister user = new UserRegister(firstName, lastName, gender, birthDate, phoneNumber, country, gpsCoordinates, email, password);


        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "You're about to experience the tAPPitz effect!", true);

        new RegisterService(user, new CallbackFromService() {
            @Override
            public void success(Object response) {
                JsonElement json = (JsonElement) response;
                TextInputLayout email_holder = (TextInputLayout)findViewById(R.id.email_holder);
                Log.d("myapp", "***get(status)***" + json.getAsJsonObject().get("status"));
                String status = json.getAsJsonObject().get("status").toString();
                if(status.equals("true")){
                    Log.d("myapp", "***get(status)**true*" );
                    email_holder.setErrorEnabled(false);
                    UsrEmail.setText(registerEmail.getText().toString());
                    UsrPassword.setText(registerPassword.getText().toString());
                    screens.add(R.id.screen_reg2);
                    showScreen(R.id.screen_login);
                }else{
                    Log.d("myapp", "***get(status)**false*" );
                    email_holder.setErrorEnabled(true);
                    String error = json.getAsJsonObject().get("error").toString();
                    email_holder.setError(error);
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
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                (year > 0)? year : now.get(Calendar.YEAR),
                (month > 0)? month : now.get(Calendar.MONTH),
                (day > 0)? day : now.get(Calendar.DAY_OF_MONTH)
        );
//        dpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void onSuccessLogin(){

        //esconde o teclado caso estiver aberto
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        //guarda o mail e pass para nao ser preciso introduzi-los a toda a hora
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        ;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Global.KEY_USER, UsrEmail.getText().toString());
        editor.putString(Global.KEY_PASS, UsrPassword.getText().toString());
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

            boolean emailValidated = emailValidator.validate(registerEmail.getText().toString());
            boolean passValidated = passwordValidator.validate(registerPassword.getText().toString());
            boolean phoneValidated = phoneValidator.validate(registerPhoneNumber.getText().toString());

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

            if(!phoneValidated) {
                phone_holder.setErrorEnabled(true);
                phone_holder.setError("Required");
            }else
                phone_holder.setErrorEnabled(false);

            return emailValidated && passValidated && phoneValidated;
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date_picker.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
        this.day = dayOfMonth;
        this.month = monthOfYear;
        this.year = year;
    }


    private boolean validators() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordValidator passwordValidator = new PasswordValidator();

        boolean emailValidated = emailValidator.validate(UsrEmail.getText().toString());
        boolean passwordValidated = passwordValidator.validate(UsrPassword.getText().toString());
        TextInputLayout textMsgWrapperUser = (TextInputLayout)findViewById(R.id.textMsgWrapperUser);
        TextInputLayout textMsgWrapperPass = (TextInputLayout)findViewById(R.id.textMsgWrapperPass);
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
