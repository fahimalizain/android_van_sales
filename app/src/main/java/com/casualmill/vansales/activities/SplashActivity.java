package com.casualmill.vansales.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.casualmill.vansales.R;
import com.casualmill.vansales.data.AppDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(getApplicationContext());
                Intent activity_intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(activity_intent);
                finish();
            }
        }, 1000);
    }
}
