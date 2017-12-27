package com.easyfixapp.easyfix.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.listeners.OnBackPressListener;

import java.util.ArrayList;
import java.util.List;


public class AccountFragment extends RootFragment {

    private View view;
    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;

    private ViewPagerAdapter mViewPagerAdapter;

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

    /**
     * Preserve icon in base fragment
     **/
    public int getChildFragmentCount(){
        try {
            Fragment fragment = mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
            return fragment.getChildFragmentManager().getBackStackEntryCount();
        } catch (Exception ignore) {}

        return 0;
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        mViewPagerAdapter.addFragment(new ProfileFragment(), "Perfil");
        mViewPagerAdapter.addFragment(new AddressFragment(), "Direcciones");
        mViewPagerAdapter.addFragment(new TechnicalHistoryFragment(), "Historial Técnico");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

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

        /**
         * On each Fragment instantiation we are saving the reference of that Fragment in a Map
         * It will help us to retrieve the Fragment by position
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        /**
         * Remove the saved reference from our Map on the Fragment destroy
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
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

    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener)
                mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            boolean isBackPressed = currentFragment.onBackPressed();
            setBackPressedIcon();

            return isBackPressed;
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }
}
