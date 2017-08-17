package com.easyfixapp.easyfix.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyfixapp.easyfix.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    public RecordFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.record_fragment, container, false);
    }

}
