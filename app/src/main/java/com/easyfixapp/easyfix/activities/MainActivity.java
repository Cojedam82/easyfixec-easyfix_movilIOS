package com.easyfixapp.easyfix.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.fragments.AgendaFragment;
import com.easyfixapp.easyfix.fragments.ConfiguracionFragment;
import com.easyfixapp.easyfix.fragments.HistorialFragment;
import com.easyfixapp.easyfix.fragments.BusquedaFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    //private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Button usuario,configuracion;
    private TextView titulo;
    private int[] arr_text = new int[]{R.string.tabBusqueda,R.string.tabAgenda,R.string.tabHistorial,R.string.tabConfiguracion};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        usuario = (Button) findViewById(R.id.main_usuario);
        configuracion = (Button) findViewById(R.id.main_configuracion);
        titulo = (TextView) findViewById(R.id.main_toolbar_text);



        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        //setSupportActionBar(toolbar);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getApplicationContext(),4);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                LinearLayout linearLayout = (LinearLayout) tab.getCustomView();
                ImageView imageView = (ImageView) linearLayout.getChildAt(0);
                imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
                TextView textView = (TextView) linearLayout.getChildAt(1);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));

                titulo.setText(arr_text[tab.getPosition()]);


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
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //tabLayout.setPadding(0,0,0,0);
        int[] arr_drawable = new int[]{android.R.drawable.ic_btn_speak_now, android.R.drawable.ic_dialog_email, android.R.drawable.ic_dialog_email, android.R.drawable.ic_dialog_email};

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(arr_drawable[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setColorFilter(ContextCompat.getColor(getApplicationContext(),i==0?R.color.colorPrimary:R.color.blackOverlayDark));
            TextView textView = new TextView(getApplicationContext());
            textView.setText(getResources().getString(arr_text[i]));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
            textView.setTextColor(getResources().getColor(i==0?R.color.colorPrimary:R.color.blackOverlayDark));
            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            linearLayout.setGravity(Gravity.CENTER);

            tabLayout.getTabAt(i).setCustomView(linearLayout);


        }

        titulo.setText(arr_text[0]);

        usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Menu1.class));
            }
        });

        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Menu2.class));
            }
        });





    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
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
                    return new BusquedaFragment();
                case 1:
                    return new AgendaFragment();
                case 2:
                    return new HistorialFragment();
                case 3:
                    return new ConfiguracionFragment();
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

}
