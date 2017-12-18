package com.casualmill.vansales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.casualmill.vansales.R;
import com.casualmill.vansales.fragments.support.FragmentLifecycle;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemSearchFragment extends Fragment implements FragmentLifecycle {


    public ItemSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_search, container, false);
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }
}
