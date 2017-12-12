package com.casualmill.vansales.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.casualmill.vansales.R;
import com.casualmill.vansales.data.DummyData;
import com.casualmill.vansales.fragments.InvoiceFragment;
import com.casualmill.vansales.fragments.ItemFragment;
import com.casualmill.vansales.fragments.TransferFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_sales:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_transfer:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_items:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadUI();
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
                        return new TransferFragment();
                    case 2:
                        return new ItemFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
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
                        id = R.id.navigation_transfer;
                        break;
                    case 2:
                        id = R.id.navigation_items;
                }
                navigation.setSelectedItemId(id);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}
