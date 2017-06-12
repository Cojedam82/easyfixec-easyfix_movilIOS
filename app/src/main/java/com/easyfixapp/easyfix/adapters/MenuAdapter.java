package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.easyfixapp.easyfix.fragments.AgendaFragment;
import com.easyfixapp.easyfix.fragments.SearchFragment;
import com.easyfixapp.easyfix.fragments.ConfigurationFragment;
import com.easyfixapp.easyfix.fragments.RecordFragment;

/**
 * Created by julio on 08/06/17.
 */

public class MenuAdapter extends FragmentPagerAdapter {
    int tabCount;
    Context mContext;


    public MenuAdapter(FragmentManager fm, Context context, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new AgendaFragment();
            case 2:
                return new RecordFragment();
            case 3:
                return new ConfigurationFragment();
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

