package com.example.gameapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MeauActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meau);
    }

    public void startGame(View view) {
        Intent intent = new Intent(MeauActivity.this, MainActivity.class);
        startActivity (intent);
    }

    public void about_us(View view) {
        Intent intent = new Intent(MeauActivity.this, AboutUsActivity.class);
        startActivity (intent);
    }
}
