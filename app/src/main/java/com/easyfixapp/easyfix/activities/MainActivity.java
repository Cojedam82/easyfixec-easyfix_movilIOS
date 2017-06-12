package com.easyfixapp.easyfix.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.adapters.MenuAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MenuAdapter mViewPagerAdapter;
    private CircleImageView mMenuProfileView, mMenuInfoView;
    private TextView mTitleView;

    private int[] arr_text = new int[]{
            R.string.tab_menu_search,
            R.string.tab_menu_agenda,
            R.string.tab_menu_record,
            R.string.tab_menu_configuration
    };

    private int[] arr_drawable = new int[]{
            R.drawable.ic_menu_search,
            R.drawable.ic_menu_agenda,
            R.drawable.ic_menu_record,
            R.drawable.ic_menu_configuration
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mTabLayout = (TabLayout) findViewById(R.id.tab_menu);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mMenuProfileView = (CircleImageView) findViewById(R.id.img_menu_profile);
        mMenuInfoView = (CircleImageView) findViewById(R.id.img_menu_info);
        mTitleView = (TextView) findViewById(R.id.txt_toolbar_title);

        /* Adding tabs */
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());

        /* View Pager */
        mViewPagerAdapter = new MenuAdapter(getSupportFragmentManager(),
                getApplicationContext(), 4);
        mViewPager.setAdapter(mViewPagerAdapter);

        /* Action Tabs */
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LinearLayout linearLayout = (LinearLayout) tab.getCustomView();

                ImageView imageView = (ImageView) linearLayout.getChildAt(0);
                imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));

                TextView textView = (TextView) linearLayout.getChildAt(1);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));

                mTitleView.setText(arr_text[tab.getPosition()]);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LinearLayout linearLayout = (LinearLayout) tab.getCustomView();

                ImageView imageView = (ImageView) linearLayout.getChildAt(0);
                imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.blackOverlayDark));

                TextView textView = (TextView) linearLayout.getChildAt(1);
                textView.setTextColor(getResources().getColor(R.color.blackOverlayDark));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        populateTabsMenu();
    }

    void populateTabsMenu(){

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {

            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(arr_drawable[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            i==0?R.color.colorPrimary:R.color.blackOverlayDark));

            TextView textView = new TextView(getApplicationContext());
            textView.setAllCaps(true);
            textView.setText(getResources().getString(arr_text[i]));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(getResources().getDimension(R.dimen.tab_menu_text_size));
            textView.setTextColor(getResources().getColor(i==0?R.color.colorPrimary:R.color.blackOverlayDark));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            linearLayout.setGravity(Gravity.CENTER);

            mTabLayout.getTabAt(i).setCustomView(linearLayout);
        }

        mTitleView.setText(arr_text[0]);
    }
}
