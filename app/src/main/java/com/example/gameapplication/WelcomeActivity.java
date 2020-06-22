package com.example.gameapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    private Timer mTimer;

    private Boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initPage();
    }

    private void initPage(){
        mTimer = new Timer(  );
        mTimer.schedule ( new TimerTask() {
            @Override
            public void run() {
                goToMain ();
                //Log.e("WelcomeActivity", "当前线程:" + Thread.currentThread ());
            }
        } , 3 * 1000);
    }

    private  void  goToMain (){
        Intent intent = new Intent ( this, MeauActivity.class );
        if(flag){
            startActivity (intent);
        }
        finish();
    }

    public void right_to_meau(View view) {
        Intent intent = new Intent ( this, MeauActivity.class );
        flag = false;
        startActivity (intent);
        finish();
    }
}
