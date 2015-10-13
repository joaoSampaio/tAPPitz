package com.tappitz.tappitz.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int[] CLICABLES = {R.id.outBtn, R.id.inBtn, R.id.friendsBtn};
//    final static int[] TAB_SELECT = {R.id.select_op_in, R.id.select_op_out, R.id.select_op_friends};
final static int[] TAB_SELECT = {R.id.textViewIN, R.id.textViewOut, R.id.textViewFriends};

    private int[] measures;
    private ViewPager viewPager;
    private int currentTab = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int id: CLICABLES){
            findViewById(id).setOnClickListener(this);
        }

        displayView(Global.HOME);

    }

    @Override
    public void onStart(){
        super.onStart();





    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



    public void displayView(int position) {
        // update the main content by replacing fragments
        Log.d("myapp", "****displayView " + position);


        //se clicarmos outra vez numa tab voltamos para HOME
        if(position == currentTab){
            position = Global.HOME;
        }

        Fragment fragment = null;
        switch (position) {

            case Global.HOME:
                fragment = new HomeFragment();
                break;
            case Global.INBOX:
                fragment = new InBoxFragment();
                break;
            case Global.OUTBOX:
                fragment = new OutBoxFragment();
                break;
            case Global.FRIENDS:
                fragment = new FriendsFragment();
                break;
            default:
                Toast.makeText(getApplicationContext(),"Somethings Wrong", Toast.LENGTH_SHORT).show();
                break;
        }


        if(fragment != null){

            //findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            currentTab = position;
            selectTab(position);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, fragment, position+"").commit();
        }
    }

    public void displayTabs(){
        findViewById(R.id.toolbar).bringToFront();


    }

    //recebe a posição da tab a selecionar e esconde as restantes
    private void selectTab(int position){
        for(int id : TAB_SELECT){
            findViewById(id).setBackgroundResource((position < TAB_SELECT.length && id == TAB_SELECT[position]) ? R.drawable.rounded_corner : 0);



//            findViewById(id).setVisibility((position < TAB_SELECT.length && id == TAB_SELECT[position]) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.inBtn:
                displayView(Global.INBOX);
                break;
            case R.id.outBtn:
                displayView(Global.OUTBOX);
                break;
            case R.id.friendsBtn:
                displayView(Global.FRIENDS);
                break;
        }
    }




}
