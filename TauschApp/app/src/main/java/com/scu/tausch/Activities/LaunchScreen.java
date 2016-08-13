package com.scu.tausch.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class LaunchScreen extends Activity {

    private TextView logo;
    String MyPREFERENCES = "MyPrefs";
    private final String keyFirstRun = "isFirstRun";
    boolean isFirstRun;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        logo = (TextView)findViewById(R.id.logoTausch);

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        isFirstRun = sharedpreferences.getBoolean(keyFirstRun,true);
        tvWelcome = (TextView)findViewById(R.id.welcomeTextView);

        if (isFirstRun){

            Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/CACChampagne.ttf");
            tvWelcome.setTypeface(typeFace);

            final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.amusement_sound);
            mp.start();
            leftToRight();

            this.findViewById(R.id.page_layout).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    mp.stop();
                    //Decision based on current status - login or logout.
                     Intent i = new Intent(LaunchScreen.this, Login.class);
                        startActivity(i);

                    return false;
                }
            });

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(keyFirstRun, false);
            editor.commit();
        }
        else{
        tvWelcome.setVisibility(View.GONE);
        leftToRight();

        //Showing splash screen.
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                //Decision based on current status - login or logout.
                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser == null) {
                    Intent i = new Intent(LaunchScreen.this, Login.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(LaunchScreen.this, HomePage.class);
                    startActivity(i);
                }


//                SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_PREFS_NAME, Context.MODE_PRIVATE);
//                if (sharedPreferences.getString("isLogin","false").equals("true")){
//                    Intent i = new Intent(LaunchScreen.this, HomePage.class);
//                    startActivity(i);
//                }
//                else {

                // }

                // close this activity
                finish();
            }
        }, Constants.LAUNCH_SCREEN_TIMEOUT);
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the mainmenu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void zoom(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myanimation);
        logo.startAnimation(animation);
    }

    public void clockwise(){
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clockwise);
        logo.startAnimation(animation1);
    }

    public void fade(){
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        logo.startAnimation(animation1);
    }

    public void blink(){
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        logo.startAnimation(animation1);
    }

    public void move(){
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        logo.startAnimation(animation1);
    }

    public void slide(){
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        logo.startAnimation(animation1);
    }
    public void leftToRight(){
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);
        logo.startAnimation(animation1);
    }


}
