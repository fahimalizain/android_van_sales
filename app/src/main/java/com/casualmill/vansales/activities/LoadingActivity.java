package com.casualmill.vansales.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.casualmill.vansales.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        boolean keep = intent.getExtras().getBoolean("keep");
        if(!keep)
        {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
