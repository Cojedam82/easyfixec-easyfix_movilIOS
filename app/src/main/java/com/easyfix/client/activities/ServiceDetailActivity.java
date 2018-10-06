package com.easyfix.client.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easyfix.client.R;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.models.Service;
import com.easyfix.client.util.Util;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by jrealpe on 27/12/17.
 */

public class ServiceDetailActivity extends AppCompatActivity {

    private AutoCompleteTextView mArtifactView;
    private ImageView mImageServiceView, mImage1View, mImage2View, mImage3View, mImage4View;
    private TabLayout mTabLayout;
    private TextView mTitleView;
    private EditText mDescriptionView;

    private Service mService = null;
    private Reservation mReservation = null;

    String[] arr_text = new String[]{
            "Instalacion",
            "Reparacion",
            "Mantenimiento"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        try {
            mReservation = getIntent().getExtras().getParcelable("reservation");
            mService = mReservation.getService();
        } catch (Exception ignore) {}

        mTitleView = findViewById(R.id.txt_service_name);
        mArtifactView = findViewById(R.id.txt_artifact);
        mDescriptionView = findViewById(R.id.txt_description);
        mImageServiceView = findViewById(R.id.img_service);
        mTabLayout = findViewById(R.id.tabs);
        mImage1View = findViewById(R.id.img_detail_1);
        mImage2View = findViewById(R.id.img_detail_2);
        mImage3View = findViewById(R.id.img_detail_3);
        mImage4View = findViewById(R.id.img_detail_4);


        // Set image from toolbar
        findViewById(R.id.img_menu_setting).setVisibility(View.INVISIBLE);

        CircleImageView mActionView = findViewById(R.id.img_menu_profile);
        mActionView.setPadding(0,0,15,0);
        mActionView.setBorderWidth(0);
        mActionView.setImageDrawable(getApplicationContext().
                getResources().getDrawable(R.drawable.ic_navigate_previous));
        mActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSupportNavigateUp();
                finish();
            }
        });

        // Set name of option
        ((TextView)findViewById(R.id.txt_toolbar_title))
                .setText("Detalles de Servicio");

        init();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.activity = this;

        Util.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
    }

    private void init() {

        if (mService != null) {

            // Populate initial info
            populateInfo();

            // Populate top tabs
            populateTabs();

            // Fill reservation
            checkReservation();

        } else {
            Util.longToast(getApplicationContext(),
                    getResources().getString(R.string.message_service_detail_empty));
        }
    }

    private void checkReservation() {

        for(int i=0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);

            //if (tab.getText().equals(mReservation.getType())) {
            //    tab.select();
            //}

            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);
            viewGroup.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        //mArtifactView.setText(mReservation.getArtifact());
        mArtifactView.setEnabled(false);

        mDescriptionView.setText(mReservation.getDescription());
        mDescriptionView.setEnabled(false);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        /*
        Glide.with(getApplicationContext())
                .load(mReservation.getImage1())
                .apply(options)
                .into(mImage1View);
        Glide.with(getApplicationContext())
                .load(mReservation.getImage2())
                .apply(options)
                .into(mImage2View);
        Glide.with(getApplicationContext())
                .load(mReservation.getImage3())
                .apply(options)
                .into(mImage3View);
        Glide.with(getApplicationContext())
                .load(mReservation.getImage4())
                .apply(options)
                .into(mImage4View);
        */
    }

    private void populateInfo(){
        // Set Background
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_settings)
                .placeholder(R.drawable.ic_settings)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getApplicationContext())
                .load(mService.getImage())
                .apply(options)
                .into(mImageServiceView);

        // Set title
        mTitleView.setText(mService.getName());

    }

    private void populateTabs(){
        mTabLayout.addTab(mTabLayout.newTab().setText(arr_text[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(arr_text[1]));
        mTabLayout.addTab(mTabLayout.newTab().setText(arr_text[2]));
        mTabLayout.getTabAt(1).select();

        for(int i=0; i < mTabLayout.getTabCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);

            // Set padding
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewGroup
                    .getLayoutParams();
            p.setMargins(10, 0, 10, 0);

            // Set font
            Typeface font = Typeface.createFromAsset(getAssets(),
                    "fonts/AntipastoRegular.otf");
            ((TextView) viewGroup.getChildAt(1)).setTypeface(font);

            viewGroup.requestLayout();
        }
    }
}
