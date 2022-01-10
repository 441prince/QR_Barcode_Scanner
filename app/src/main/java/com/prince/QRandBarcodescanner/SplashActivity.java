package com.prince.QRandBarcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.nextActivity();
            }
        }, 1000);
    }

    public void nextActivity() {
        startActivity(new Intent(this, ScannedBarcodeActivity.class));
        finish();
    }
}