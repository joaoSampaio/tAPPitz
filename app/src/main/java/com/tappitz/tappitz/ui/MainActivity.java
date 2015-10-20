package com.tappitz.tappitz.ui;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tappitz.tappitz.Global;
import com.tappitz.tappitz.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int[] CLICABLES = {R.id.outBtn, R.id.inBtn, R.id.friendsBtn};
//    final static int[] TAB_SELECT = {R.id.select_op_in, R.id.select_op_out, R.id.select_op_friends};
final static int[] TAB_SELECT = {R.id.textViewIN, R.id.textViewOut, R.id.textViewFriends};

    private RelativeLayout toolbar;
    private int[] measures;
    private ViewPager viewPager;
    private int currentTab = -1;
    private AnimatorSet set;
    private View loading;
    private boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int id: CLICABLES){
            findViewById(id).setOnClickListener(this);
        }
        toolbar = (RelativeLayout)findViewById(R.id.toolbar);
        loading = findViewById(R.id.loading);
        //showLoadingScreen();


        Intent intent = getIntent();
        String action = null;
        Log.d("myapp", "****onCreate " + intent.hasExtra("action"));
        if(intent.getExtras() != null){
            Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

        }

        if(intent.hasExtra("action"))
            action = intent.getExtras().getString("action");
        if(action != null){
            Log.d("myapp", "****action: " + action);
            switch (action){

                case Global.NOTIFICATION_ACTION_INVITE:
                    displayView(Global.FRIENDS);
                    break;
                case Global.NOTIFICATION_ACTION_NEW_PHOTO:
                    displayView(Global.INBOX);
                    break;
            }

        }else{
            displayView(Global.HOME);
        }


//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//
//                try {
//                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//
//                    String deviceToken = null;
//                    try {
//                        deviceToken = gcm.register(Global.PROJECT_ID);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("GCM", "Device token : " + deviceToken);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//        }.execute();

    }

    @Override
    public void onStart(){
        super.onStart();
        //showLoadingScreen();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = null;
        Log.d("myapp", "****onNewIntent " + intent.hasExtra("action"));
        if(intent.getExtras() != null){
            Log.d("myapp", "****getExtras: " + intent.getExtras().getString("action"));

        }

        if(intent.hasExtra("action"))
            action = intent.getExtras().getString("action");
        if(action != null){
            Log.d("myapp", "****action: " + action);
            switch (action){

                case Global.NOTIFICATION_ACTION_INVITE:
                    displayView(Global.FRIENDS);
                    break;
                case Global.NOTIFICATION_ACTION_NEW_PHOTO:
                    displayView(Global.INBOX);
                    break;
            }

        }


    }

    private void showLoadingScreen(){

        this.isLoading = true;
        loading.setVisibility(View.VISIBLE);
        loading.bringToFront();
//        View iconR = findViewById(R.id.iconR);
//        View iconY = findViewById(R.id.iconY);
//        View iconG = findViewById(R.id.iconG);
//        set = new AnimatorSet();
//
//        set.play(getIntroBallAnim(iconR, 0));
//        set.play(getIntroBallAnim(iconY, 500));
//        set.play(getIntroBallAnim(iconG, 1000));
//        set.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //set.cancel();

                loading.setVisibility(View.GONE);
                isLoading = false;
                displayTabs(true);
            }
        }, 1500);


    }

    private ObjectAnimator getIntroBallAnim(View v, int delay){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v, "translationY", -200f);
        animator1.setRepeatCount(1);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.setDuration(1000);
        animator1.setStartDelay(delay);
        return animator1;
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

    public void displayTabs(boolean show){
        if(!isLoading) {
            toolbar.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                toolbar.bringToFront();
            }
        }

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


    @Override
    public void onBackPressed() {
        if(currentTab != Global.HOME)
            displayView(Global.HOME);
        else{
            finish();
        }
    }

}
