package com.tappitz.tappitz.paginasRegisto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.validators.EmailValidator;
import com.tappitz.tappitz.validators.PasswordValidator;
import com.tappitz.tappitz.validators.PhoneValidator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by X220 on 11/06/2015.
 */
public class PaginaRegisto3 extends Activity {

    EditText email, password, phoneNumber;
    Button backBtn, registerBtn;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paginaregisto3);

        initialize();
    }

    private boolean validators() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordValidator passwordValidator = new PasswordValidator();
        PhoneValidator phoneValidator = new PhoneValidator();

        boolean emailValidated = emailValidator.validate(email.getText().toString());
        boolean passwordValidated = passwordValidator.validate(password.getText().toString());
        boolean phoneValidated = phoneValidator.validate(phoneNumber.getText().toString());

        return emailValidated && passwordValidated && phoneValidated;

        /* if (emailValidated && passwordValidated && phoneValidated) {

            registerBtn.setEnabled(true);
        } else {
            registerBtn.setEnabled(false);
        } */

    }


    private void initialize() {

        email = (EditText) findViewById(R.id.registerEmail);
        password = (EditText) findViewById(R.id.registerPassword);
        phoneNumber = (EditText) findViewById(R.id.registerPhoneNumber);
        backBtn = (Button) findViewById(R.id.backToRegister2Page);
        registerBtn = (Button) findViewById(R.id.nextToFinnishRegister);


        Intent intent = getIntent();

        String UsrEmail = intent.getStringExtra("UsrEmail");
        String UsrPassword = intent.getStringExtra("UsrPassword");

        email.setText(UsrEmail);
        password.setText(UsrPassword);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //enviar os dados para o servidor

// Note: declare ProgressDialog progress as a field in your class.


                if (validators()) {

                    //When you're ready to start the progress dialog:
                    progress = ProgressDialog.show(PaginaRegisto3.this, "Please wait", "You're about to experience the tAPPitz effect!", true);

                    new Thread() {

                        @Override
                        public void run() {
                            // do the thing that takes a long time

                            Intent intent = getIntent();

                            String primeiroNome = intent.getStringExtra("primeiroNome");
                            String ultimoNome = intent.getStringExtra("ultimoNome");
                            Boolean genero = intent.getBooleanExtra("genero", false);
                            int paisSeleccionado = intent.getIntExtra("Pais", 0);
                            int mesSeleccionado = intent.getIntExtra("Mes", 0);
                            String ano = intent.getStringExtra("ano");
                            String dia = intent.getStringExtra("dia");



                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("http://176.111.104.39:8182/users");

                            httppost.setHeader("Content-type", "application/json");
                            httppost.setHeader("Accept", "application/json");

                            try {
                                JSONObject jsonParams = new JSONObject();
                                jsonParams.put("firstName", primeiroNome);
                                jsonParams.put("lastName", ultimoNome);
                                jsonParams.put("gender", genero.toString());
                                jsonParams.put("birthDate", dia + "_" + mesSeleccionado + "_" + ano);
                                jsonParams.put("phoneNumber", phoneNumber.getText().toString());
                                jsonParams.put("country", paisSeleccionado + "");
                                jsonParams.put("gpsCoordinates", "123");
                                jsonParams.put("email", email.getText().toString());
                                jsonParams.put("password", password.getText().toString());

                                httppost.setEntity(new StringEntity(jsonParams.toString(), "UTF-8"));

                                // Execute HTTP Post Request
                                HttpResponse response = httpclient.execute(httppost);
                                final String responseStr = EntityUtils.toString(response.getEntity());
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            final JSONObject res = new JSONObject(responseStr);
                                            if (res.getString("status").equals("true")) {
                                                Toast.makeText(getApplicationContext(), "Registration successfull!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                                                Log.d("myapp", res.getString("error"));
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "An error occurred, please check your internet connection.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } catch (final Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "An error occurred, please check your internet connection.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            // go away when you're done
                            progress.dismiss();

                        }
                    }.start();

                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                //Intent intent = new Intent(v.getContext(), PaginaRegisto2.class);
                //startActivityForResult(intent, 0);

            }
        });

    }
}
