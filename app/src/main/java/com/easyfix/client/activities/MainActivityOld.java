package com.easyfix.client.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.easyfix.client.R;
import com.easyfix.client.adapters.ViewPagerAdapter;
import com.easyfix.client.util.SessionManager;
import com.easyfix.client.util.Util;
import com.easyfix.client.widget.CustomViewPager;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivityOld extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CustomViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CircleImageView mMenuProfileView, mMenuSettingView;
    private TextView mTitleView;
    private MenuItem prevMenuItem;
    private BottomNavigationView bottomNavigationView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);

        mViewPager = (CustomViewPager) findViewById(R.id.view_pager);
        mMenuProfileView = (CircleImageView) findViewById(R.id.img_menu_profile);
        mMenuSettingView = (CircleImageView) findViewById(R.id.img_menu_setting);
        mTitleView = (TextView) findViewById(R.id.txt_toolbar_title);


        /* Settings */
        mMenuSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.END);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        addNavigationAction(navigationView);

        /* Menu */
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.navigation_service);

        /* View Pager */
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
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

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //if (id == R.id.nav_policies) {
            // Handle the camera action
        //} else
        if (id == R.id.nav_terms) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void addNavigationAction(NavigationView navigationView) {
        for (int i=0; i<navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setActionView(R.layout.item_arrow);
    }

    /**
     *
     * Settings Actions
     *
     **/
    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityOld.this, R.style.AlertDialog);
        builder.setMessage(R.string.message_logout_dialog)
                .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Clean preferences
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.clear();

                        Intent intent = new Intent(MainActivityOld.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                        Util.longToast(getApplicationContext(), getString(R.string.message_logout));
                    }
                })
                .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();
    }
}
