package com.easyfixapp.easyfix.fragments;

import android.support.v4.app.Fragment;

import com.easyfixapp.easyfix.listeners.OnBackPressListener;
import com.easyfixapp.easyfix.listeners.BackPress;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPress(this).onBackPressed();
    }
}
