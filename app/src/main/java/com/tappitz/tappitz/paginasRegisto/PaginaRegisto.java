package com.tappitz.tappitz.paginasRegisto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.HomePage;
import com.tappitz.tappitz.R;
import com.tappitz.tappitz.validators.EmailValidator;
import com.tappitz.tappitz.validators.PasswordValidator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by X220 on 16/04/2015.
 */
public class PaginaRegisto extends Activity {

    //1) definem-se as variaveis
    Button regBtn, loginBtn;
    EditText UsrEmail, UsrPassword;
    ProgressDialog progress;

    //2) Implementa-se esta coisa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //3) Diz-se qual e' a vista que este Java tem de ir buscar no XML
        setContentView(R.layout.paginaregisto);

        //4) Diz-se onde ir buscar os botoes e etc
        regBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        UsrEmail = (EditText) findViewById(R.id.loginEmail);
        UsrPassword = (EditText) findViewById(R.id.loginPassword);


        //5) define accoes para os botoes. Faz-se a magia

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), PaginaRegisto2.class);

                intent.putExtra("UsrEmail", UsrEmail.getText().toString());
                intent.putExtra("UsrPassword", UsrPassword.getText().toString());

                startActivityForResult(intent, 0);

            }


        });

//falta adicionar o: se o user fica sem net, ele nao pode crashar no progressBar
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Intent intent = new Intent(v.getContext(), HomePage.class);
                startActivityForResult(intent, 0);

                if (validators()) {

                    //verifica se o loggin esta correctamente feito...

                    //When you're ready to start the progress dialog:
                    progress = ProgressDialog.show(PaginaRegisto.this, "Please wait", "You're about to experience the tAPPitz effect!", true);

                    new Thread() {

                        @Override
                        public void run() {
                            // do the thing that takes a long time

                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("http://176.111.104.39:8182/users/login");

                            httppost.setHeader("Content-type", "application/json");
                            httppost.setHeader("Accept", "application/json");

                            try {
                                JSONObject jsonParams = new JSONObject();
                                jsonParams.put("email", UsrEmail.getText().toString());
                                jsonParams.put("password", UsrPassword.getText().toString());

                                httppost.setEntity(new StringEntity(jsonParams.toString(), "UTF-8"));
                                // serviu para para testar Log.i("LOLOLOL", new BufferedReader(new InputStreamReader(new StringEntity(jsonParams.toString(), "UTF-8").getContent())).readLine());

                                // Execute HTTP Post Request
                                HttpResponse response = httpclient.execute(httppost);
                                final String responseStr = EntityUtils.toString(response.getEntity());
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            final JSONObject res = new JSONObject(responseStr);
                                            if (res.getString("status").equals("true")) {

                                                Global.sessionKey = res.getString("sessionKey");
                                                Toast.makeText(getApplicationContext(), "Login successfull!", Toast.LENGTH_LONG).show();

                                                //Se o loggin for realizado com sucesso, aqui encaminhara' para o homepage.
                                                Intent intent = new Intent(v.getContext(), HomePage.class);
                                                startActivityForResult(intent, 0);

                                            } else {
                                                Toast.makeText(getApplicationContext(), res.getString("error"), Toast.LENGTH_LONG).show();
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

    };

//fim da validacao do loggin



    private boolean validators() {
        EmailValidator emailValidator = new EmailValidator();
        PasswordValidator passwordValidator = new PasswordValidator();

        boolean emailValidated = emailValidator.validate(UsrEmail.getText().toString());
        boolean passwordValidated = passwordValidator.validate(UsrPassword.getText().toString());

        return emailValidated && passwordValidated;

        /* if(emailValidated && passwordValidated) {

            loginBtn.setEnabled(true);
        }
        else {
            loginBtn.setEnabled(false);
        } */

    }


    //@Override
    //protected void onPause() {
    //    super.onPause();
    //    finish();
    // }

}
