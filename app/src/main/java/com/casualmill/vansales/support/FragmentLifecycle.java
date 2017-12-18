package com.casualmill.vansales.support;

/**
 * Created by faztp on 18-Dec-17.
 * To Notify pages of their state
 * onPause and onResume
 */

public interface FragmentLifecycle {
    void onPauseFragment();
    void onResumeFragment();
}
