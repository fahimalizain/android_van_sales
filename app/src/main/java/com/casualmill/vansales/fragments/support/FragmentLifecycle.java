package com.casualmill.vansales.fragments.support;

import android.support.v4.view.ViewPager;

/**
 * Created by faztp on 18-Dec-17.
 * To Notify pages of their state
 * onPause and onResume
 */

public interface FragmentLifecycle {
    void onPauseFragment();
    void onResumeFragment();
}
