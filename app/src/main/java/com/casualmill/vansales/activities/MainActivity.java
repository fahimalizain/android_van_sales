package com.casualmill.vansales.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.casualmill.vansales.R;
import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.SyncData;
import com.casualmill.vansales.support.MainActivityEvent;
import com.casualmill.vansales.fragments.InvoiceFragment;
import com.casualmill.vansales.fragments.ItemFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private int loadingCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.destroyAppDatabase();
    }

    protected void LoadUI() {
        // Dummy Data
        // DummyData.AddDummyItems();

        // Viewpager
        viewPager = findViewById(R.id.main_viewPager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new InvoiceFragment();
                    case 1:
                        return new ItemFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        viewPager.setAdapter(adapter);


        // Bottom Nav
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                int id = -1;
                switch (position) {
                    case 0:
                        id = R.id.navigation_sales;
                        break;
                    case 1:
                        id = R.id.navigation_items;
                }
                navigation.setSelectedItemId(id);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                EventBus.getDefault().post(new MainActivityEvent(MainActivityEvent.EventType.Refresh));
                break;
            case R.id.menu_sync :
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                new SyncData(this).execute(sp.getString("server_address", "10.0.2.2:18565"), sp.getString("vehicle_id", "VAN12345"));
                break;
            case R.id.menu_settings :
                Intent in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                break;
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_sales:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_items:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onMainActivityEvent(MainActivityEvent event) {
        if (event.type == MainActivityEvent.EventType.START_LOADING)
        {
            if (loadingCount == 0)
                LoadingActivity.Start(this);
            loadingCount++;
        } else if (event.type == MainActivityEvent.EventType.STOP_LOADING) {
            loadingCount = Math.max(0,loadingCount - 1);
            if (loadingCount == 0)
                LoadingActivity.Stop(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}

