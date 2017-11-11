package com.easyfixapp.easyfix.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.listeners.BackPress;
import com.easyfixapp.easyfix.listeners.OnBackPressListener;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPress(this).onBackPressed();
    }

    public void setBackPressedIcon(){
        Activity activity = getActivity();
        ImageView mActionView = activity.findViewById(R.id.img_menu_profile);
        mActionView.setPadding(0,0,15,0);
        mActionView.setImageDrawable(activity.
                getResources().getDrawable(R.drawable.ic_navigate_previous));
    }
}
