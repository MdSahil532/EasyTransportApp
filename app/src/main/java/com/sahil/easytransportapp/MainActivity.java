package com.sahil.easytransportapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;

public class MainActivity extends AppCompatActivity {

    TextView easyTextView, transTextView;
    ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easyTextView = findViewById(R.id.textView3);
        transTextView = findViewById(R.id.textView2);
        logoImageView = findViewById(R.id.imageView);

        easyTextView.animate().alpha(1).setDuration(1500);
        transTextView.animate().alpha(1).setDuration(1500);
        logoImageView.animate().alpha(1).setDuration(1500);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();

            }
        },   2000);



        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}



