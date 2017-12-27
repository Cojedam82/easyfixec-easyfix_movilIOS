package com.easyfixapp.easyfix.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.listeners.BackPress;
import com.easyfixapp.easyfix.listeners.OnBackPressListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPress(this).onBackPressed();
    }

    public void setBackPressedIcon(){
        Activity activity = getActivity();
        CircleImageView mActionView = activity.findViewById(R.id.img_menu_profile);
        mActionView.setPadding(0,0,15,0);
        mActionView.setImageDrawable(activity.
                getResources().getDrawable(R.drawable.ic_navigate_previous));
        mActionView.setBorderWidth(0);
    }
}
