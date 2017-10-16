package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.easyfixapp.easyfix.fragments.AccountFragment;
import com.easyfixapp.easyfix.fragments.NotificationFragment;
import com.easyfixapp.easyfix.fragments.ServiceFragment;
import com.easyfixapp.easyfix.fragments.SettingFragment;

/**
 * Created by julio on 08/06/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    Context mContext;


    public ViewPagerAdapter(FragmentManager fm, Context context, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new ServiceFragment();
            case 1:
                return new NotificationFragment();
            case 2:
                return new AccountFragment();
            case 3:
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return tabCount;

    }
}

