/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casualmill.vansales.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.casualmill.vansales.R;
import com.casualmill.vansales.fragments.BarcodeScanner;
import com.casualmill.vansales.fragments.ItemSearchFragment;
import com.casualmill.vansales.support.FragmentLifecycle;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class ItemSearchActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_item_search);

        ViewPager pg = findViewById(R.id.itemsearch_rootViewPager);
        TabLayout tl = findViewById(R.id.itemsearch_tabLayout);
        tl.setupWithViewPager(pg);
        final PagerAdapter pAdapter = new PagerAdapter(getSupportFragmentManager());
        pg.setAdapter(pAdapter);

        pg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int currentPosition = 0;

            @Override
            public void onPageSelected(int newPosition) {
                FragmentLifecycle fragmentToShow = (FragmentLifecycle)pAdapter.getItem(newPosition);
                fragmentToShow.onResumeFragment();

                FragmentLifecycle fragmentToHide = (FragmentLifecycle)pAdapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                currentPosition = newPosition;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new BarcodeScanner();
            else
                return new ItemSearchFragment();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Barcode";
            else
                return "Search";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}