package com.easyfixapp.easyfix.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Settings;
import com.easyfixapp.easyfix.util.Util;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {

    private TextView mTitleView;
    private WebView mDescriptionView;
    private Settings settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = (Settings) getIntent().getExtras().getSerializable("settings");

        mTitleView = findViewById(R.id.txt_title);
        mDescriptionView = findViewById(R.id.wb_description);
        mDescriptionView.setBackgroundColor(0x00000000);

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
                .setText(getString(R.string.tab_menu_setting));

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
    }

    public boolean onSupportNavigateUp(){
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            NavUtils.navigateUpTo(this, upIntent);
        }
        return true;
    }


    /**
     * Initialize user and actions
     */
    public void init() {

        if (settings != null) {
            mTitleView.setText(settings.getName());

            String mimeType = "text/html";
            String encoding = "UTF-8";
            String url = settings.getUrl();
            //mDescriptionView.loadData(url, mimeType, encoding);
            mDescriptionView.loadUrl(url);

        } else {
            Util.longToast(getApplicationContext(), getString(R.string.message_service_server_failed));
        }
    }
}
