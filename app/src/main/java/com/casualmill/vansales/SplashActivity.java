package com.casualmill.vansales;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent activity_intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(activity_intent);
                finish();
            }
        }, 1000);
    }
}
