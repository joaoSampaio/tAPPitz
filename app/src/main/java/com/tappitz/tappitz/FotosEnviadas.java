package com.tappitz.tappitz;

import android.app.Activity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by X220 on 22/06/2015.
 */
public class FotosEnviadas extends Activity {

    ImageView fotoEnviada;
    TextView textoDescricaoFotoEnviada;
    LinearLayout painelVotacao;
    Button verde, amarelo, vermelho;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotosenviadas);

        fotoEnviada = (ImageView) findViewById(R.id.fotoEnviada);
        textoDescricaoFotoEnviada = (TextView) findViewById(R.id.textoDescricaoFoto);
        painelVotacao = (LinearLayout) findViewById(R.id.painelvotacao);
        verde = (Button) findViewById(R.id.botaoVerde);
        amarelo = (Button) findViewById(R.id.botaoAmarelo);
        vermelho = (Button) findViewById(R.id.botaoVermelho);





        verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //encaminhar para o listadapter dos comentarios.


            }
        });

    }
}
