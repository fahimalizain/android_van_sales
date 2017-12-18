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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.casualmill.vansales.R;
import com.casualmill.vansales.camera.BarcodeTracker;
import com.casualmill.vansales.camera.BarcodeTrackerFactory;
import com.casualmill.vansales.camera.CameraSourcePreview;
import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.DataHelper;
import com.casualmill.vansales.data.dao.ItemDao;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.fragments.BarcodeScanner;
import com.casualmill.vansales.fragments.ItemSearchFragment;
import com.casualmill.vansales.fragments.support.FragmentLifecycle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

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