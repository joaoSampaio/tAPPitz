package com.tappitz.tappitz.paginasRegisto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.tappitz.tappitz.R;
import com.tappitz.tappitz.validators.NameValidator;

/**
 * Created by X220 on 28/04/2015.
 */
public class PaginaRegisto2 extends Activity {

    EditText primeiroNome, ultimoNome, diaAniversario, anoAniversario;
    Spinner mesesSpinner, paisesSpinner;
    Button btBack, btNext;
    int paisSeleccionado, mesSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paginaregisto2);
        initialize();
    }

        private boolean validators (){
            NameValidator nameValidator = new NameValidator();

            boolean primeiroNomeValidated = nameValidator.validate(primeiroNome.getText().toString());
            boolean ultimoNomeValidado = nameValidator.validate(ultimoNome.getText().toString());

            return primeiroNomeValidated && ultimoNomeValidado;

            /*
            if(primeiroNomeValidated && ultimoNomeValidado) {

                btNext.setEnabled(true);
            }
            else {
                btNext.setEnabled(false);
            } */


        }



    private void initialize() {
        primeiroNome = (EditText) findViewById(R.id.firstname);
        ultimoNome = (EditText) findViewById(R.id.lastname);
        btBack = (Button) findViewById(R.id.backToLoginPage);
        btNext = (Button) findViewById(R.id.nextToRegister3);
        diaAniversario = (EditText) findViewById(R.id.dia);
        anoAniversario = (EditText) findViewById(R.id.ano);

        //este codigo faz com que o teclado apareca automaticamente
        primeiroNome.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(primeiroNome, InputMethodManager.SHOW_IMPLICIT);

        //definicao dos spinner para os meses do ano
        mesesSpinner = (Spinner) findViewById(R.id.mesesSpinner);
        String[] meses = null;// getResources().getStringArray(R.array.meses);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, meses);
        mesesSpinner.setAdapter(adapter);

        //definicao dos spinner para os paises
        paisesSpinner = (Spinner) findViewById(R.id.counties);
        String[] paises = getResources().getStringArray(R.array.paises);
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paises);
        paisesSpinner.setAdapter(adapterC);


        //codigo dos spinner para os meses
        mesesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mesSelecionado = parent.getSelectedItemPosition();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//codigo dos spinner para os paises
        paisesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        paisSeleccionado = parent.getSelectedItemPosition();
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }

                                                }

        );


//Inicio de codigo: Botoes para avancar ou retroceder nas paginas
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validators()){



                boolean genero;
                RadioButton female = ((RadioButton) findViewById(R.id.female));
                RadioButton male = ((RadioButton) findViewById(R.id.male));
                if (male.isChecked())
                    genero = true;
                else
                    genero = false;



                    Intent prevIntent = getIntent();

                    Intent intent = new Intent(v.getContext(), PaginaRegisto3.class);

                    //Estes dois estao a chegar da pagina anterior para poder registar o seu valor na proxima pagina (Reg3)


                intent.putExtra("UsrEmail", prevIntent.getStringExtra("UsrEmail"));
                intent.putExtra("UsrPassword", prevIntent.getStringExtra("UsrPassword"));

                    //Estes estes validators sao os que envio desta pagina para a (Reg3)
                intent.putExtra("primeiroNome", primeiroNome.getText().toString());
                intent.putExtra("ultimoNome", ultimoNome.getText().toString());
                intent.putExtra("genero", genero);
                intent.putExtra("Pais", paisSeleccionado);
                intent.putExtra("Mes", mesSelecionado);
                intent.putExtra("ano", anoAniversario.getText().toString());
                intent.putExtra("dia", diaAniversario.getText().toString());

                startActivityForResult(intent, 0);

                }
               else {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();


            }
        });
//Fim de codigo: Botoes para avancar ou retroceder nas paginas


    }
}



