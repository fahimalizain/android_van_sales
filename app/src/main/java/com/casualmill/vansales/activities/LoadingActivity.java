package com.casualmill.vansales.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.casualmill.vansales.R;
import com.casualmill.vansales.support.MainActivityEvent;

import org.greenrobot.eventbus.EventBus;

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
        if (intent == null || intent.getExtras() == null)
            return;
        boolean keep = intent.getExtras().getBoolean("keep", true);
        if(!keep)
        {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public static Intent getIntent(Context ctx) {
        Intent intent = new Intent(ctx, LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public static void Start(Context ctx) {
        ctx.startActivity(getIntent(ctx));
    }

    public static void Stop(Context ctx) {
        ctx.startActivity(getIntent(ctx).putExtra("keep", false));
    }

    public static void IncrementLoading() {
        EventBus.getDefault().post(new MainActivityEvent(MainActivityEvent.EventType.START_LOADING));
    }

    public static void DecrementLoading() {
        EventBus.getDefault().post(new MainActivityEvent(MainActivityEvent.EventType.STOP_LOADING));
    }
}
