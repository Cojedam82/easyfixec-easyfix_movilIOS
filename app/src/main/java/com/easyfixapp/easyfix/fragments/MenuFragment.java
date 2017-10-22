package com.easyfixapp.easyfix.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.LoginActivity;
import com.easyfixapp.easyfix.adapters.ViewPagerAdapter;
import com.easyfixapp.easyfix.listeners.OnBackPressListener;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;
import com.easyfixapp.easyfix.widget.CustomViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CircleImageView mMenuProfileView, mMenuSettingView;
    private TextView mTitleView;
    private MenuItem prevMenuItem;
    private BottomNavigationView bottomNavigationView;
    private View view;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String title = item.getTitle().toString();
            switch (item.getItemId()) {
                case R.id.navigation_service:
                    mViewPager.setCurrentItem(0);
                    mTitleView.setText(title);
                    return true;
                case R.id.navigation_notification:
                    mViewPager.setCurrentItem(1);
                    mTitleView.setText(title);
                    return true;
                case R.id.navigation_account:
                    mViewPager.setCurrentItem(2);
                    mTitleView.setText(title);
                    return true;
            }
            return false;
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        mViewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        mMenuProfileView = (CircleImageView) view.findViewById(R.id.img_menu_profile);
        mMenuSettingView = (CircleImageView) view.findViewById(R.id.img_menu_setting);
        mTitleView = (TextView) view.findViewById(R.id.txt_toolbar_title);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Settings */
        mMenuSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.END);
            }
        });
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addNavigationAction(navigationView);

        /* Menu */
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_service);

        /* View Pager */
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),
                bottomNavigationView.getMenu().size());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("Menu Tab", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
            return true;
        } else {
            // currently visible tab Fragment
            OnBackPressListener currentFragment = (OnBackPressListener)
                    mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());

            if (currentFragment != null) {
                // lets see if the currentFragment or any of its childFragment can handle onBackPressed
                return currentFragment.onBackPressed();
            }
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_policies) {
            // Handle the camera action
        } else if (id == R.id.nav_terms) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void addNavigationAction(NavigationView navigationView) {
        for (int i=0; i<navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setActionView(R.layout.horizontal_arrow);
    }

    /**
     *
     * Settings Actions
     *
     **/
    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        builder.setMessage(R.string.message_logout_dialog)
                .setPositiveButton(R.string.message_dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Clean preferences
                        SessionManager sessionManager = new SessionManager(getContext());
                        sessionManager.clear();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        Util.longToast(getContext(), getString(R.string.message_logout));
                    }
                })
                .setNegativeButton(R.string.message_dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();
    }
}
