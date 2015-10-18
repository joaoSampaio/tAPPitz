package com.tappitz.tappitz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by X220 on 16/04/2015.
 */
public class PaginaVotacao extends Activity {

    //1) definem-se as variaveis
    Button verde, amarelo, vermelho;
    TextView descricaoFoto, textoResposta;
    ImageView corPressionada, fotos;
    LinearLayout painelVotacao;
    boolean longPress = false;

    final View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            longPress = true;
            // a ideia aqui e fazer com que este editText fique activo se o user ficar a pressionar na cor, inactivando o ACTION_UP
            textoResposta.setVisibility(View.VISIBLE);
            textoResposta.requestFocus();
            textoResposta.bringToFront();
                    //este codigo faz com que o teclado apareca automaticamente
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textoResposta, InputMethodManager.SHOW_IMPLICIT);

                                //para fechar e' preciso usar este codigo
                // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);

            return false;
        }
    };

    //2) Implementa-se esta coisa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //3) Diz-se qual e' a vista que este Java tem de ir buscar no XML
        setContentView(R.layout.paginavotacao);

        //4) Diz-se onde ir buscar os botoes e etc
        verde = (Button) findViewById(R.id.botaoVerde);
        amarelo = (Button) findViewById(R.id.botaoAmarelo);
        vermelho = (Button) findViewById(R.id.botaoVermelho);
        descricaoFoto = (TextView) findViewById(R.id.textoDescricaoFoto);
        corPressionada = (ImageView) findViewById(R.id.fundoEmFuncaoDaCor);
        fotos = (ImageView) findViewById(R.id.foto);
        painelVotacao = (LinearLayout) findViewById(R.id.painelvotacao);
        textoResposta=(EditText) findViewById(R.id.textoResposta);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        corPressionada.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//
//    //5) define accoes para os tres botoes das cores. Faz-se a magia
//
//        verde.setOnLongClickListener(longClickListener);
//
//        verde.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        longPress = false;
//
//                        corPressionada.setBackgroundColor(getResources().getColor(R.color.green));
//                        corPressionada.bringToFront();
//                        return false; // if you want to handle the touch event (o que e' que isto quer dizer?)
//
//                    case MotionEvent.ACTION_UP:
//                        if (longPress)
//                            return true;
//
//                        //Colocar aqui o envio da pontuacao e encaminhar para ou: 1) paginaMensagens / 2) fora da App
//
//                        Intent intent = new Intent(v.getContext(), HomePage.class);
//                        startActivityForResult(intent, 0);
//
//                        return true; // if you want to handle the touch event (o que e' que isto quer dizer?)
//
//                }
//                return false;
//            }
//        });
//
//
//        amarelo.setOnLongClickListener(longClickListener);
//
//        amarelo.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        longPress = false;
//
//                        corPressionada.setBackgroundColor(getResources().getColor(R.color.yellow));
//                        corPressionada.bringToFront();
//                        return false; // if you want to handle the touch event (o que e' que isto quer dizer?)
//
//                    case MotionEvent.ACTION_UP:
//                        if (longPress)
//                            return true;
//                        //Colocar aqui o envio da pontuacao e encaminhar para ou: 1) paginaMensagens / 2) fora da App
//
//                        Intent intent = new Intent(v.getContext(), HomePage.class);
//                        startActivityForResult(intent, 0);
//
//                        return true; // if you want to handle the touch event (o que e' que isto quer dizer?)
//
//                }
//                return false;
//
//            }
//        });
//
//        vermelho.setOnLongClickListener(longClickListener);
//
//        vermelho.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                    switch(event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            longPress = false;
//
//                            corPressionada.setBackgroundColor(getResources().getColor(R.color.red));
//                            corPressionada.bringToFront();
//                            return false; // if you want to handle the touch event (o que e' que isto quer dizer?)
//
//                        case MotionEvent.ACTION_UP:
//                            if (longPress)
//                                return true;
//                            //Colocar aqui o envio da pontuacao e encaminhar para ou: 1) paginaMensagens / 2) fora da App
//
//                            Intent intent = new Intent(v.getContext(), HomePage.class);
//                            startActivityForResult(intent, 0);
//
//                            return true; // if you want to handle the touch event (o que e' que isto quer dizer?)
//
//                    }
//                    return false;
//            }
//        });




    //fazer vir ao de cima a foto quando esta esta a ser pressionada
        fotos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(longPress)
                    return false;

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        descricaoFoto.setVisibility(View.INVISIBLE);
                        painelVotacao.setVisibility(View.INVISIBLE);
                        verde.setVisibility(View.INVISIBLE);
                        amarelo.setVisibility(View.INVISIBLE);
                        vermelho.setVisibility(View.INVISIBLE);

                        return true;

                    case MotionEvent.ACTION_UP:

                        descricaoFoto.setVisibility(View.VISIBLE);
                        painelVotacao.setVisibility(View.VISIBLE);
                        verde.setVisibility(View.VISIBLE);
                        amarelo.setVisibility(View.VISIBLE);
                        vermelho.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        });

    }

 }





