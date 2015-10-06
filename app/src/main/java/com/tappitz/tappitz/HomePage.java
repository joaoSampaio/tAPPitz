package com.tappitz.tappitz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.tappitz.tappitz.cameraAccess.CameraControl;

/**
 * Created by X220 on 17/04/2015.
 */
public class HomePage extends Activity {

    Button takephoto, settings, friends, photosent;

    private ListView ListView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        takephoto = (Button)findViewById(R.id.CameraBtn);
        settings = (Button)findViewById(R.id.SettingsBtn);
        friends = (Button)findViewById(R.id.FriendsBtn);
        photosent = (Button)findViewById(R.id.PictureSentBtn);



        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //PROVISORIO.

                Intent intent = new Intent(v.getContext(), CameraControl.class);
                startActivityForResult(intent, 0);

            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //inserir aqui a accao: encaminhar para a pagina de Settings

                //Provisorio
                Intent intent = new Intent(v.getContext(), PaginaVotacao.class);
                startActivityForResult(intent, 0);

            }
        });

        photosent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), FotosEnviadas.class);
                startActivityForResult(intent, 0);

            }
        });


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //inserir aqui a accao: encaminhar para a pagina de Amigos

            }
        });


        //finish();
    }
}
