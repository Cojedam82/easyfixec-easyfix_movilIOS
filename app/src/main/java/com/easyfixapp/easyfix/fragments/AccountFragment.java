package com.easyfixapp.easyfix.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;

import java.util.ArrayList;
import java.util.List;


public class AccountFragment extends RootFragment {

    private View view;
    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;

    public AccountFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new ProfileFragment(), "Perfil");
        adapter.addFragment(new AddressFragment(), "Direcciones");
        adapter.addFragment(new TechnicalHistoryFragment(), "Historial Técnico");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void init() {
        populateViewPager();

        // Populate tab style
        populateTabs();
    }

    private void populateTabs() {
        for(int i=0; i < mTabLayout.getTabCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);

            // Set padding
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            p.setMargins(10, 0, 10, 0);

            // Set font
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AntipastoRegular.otf");
            ((TextView) viewGroup.getChildAt(1)).setTypeface(font);

            viewGroup.requestLayout();
        }
    }

    private void populateViewPager() {

        if (mViewPager == null) {
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mViewPager.setOffscreenPageLimit(3);
            setupViewPager(mViewPager);
        }

        if (mTabLayout == null) {
            mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }
}
