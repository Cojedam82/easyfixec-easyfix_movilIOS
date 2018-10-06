package com.easyfix.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.easyfix.client.BuildConfig;
import com.easyfix.client.R;
import com.easyfix.client.models.Settings;
import com.easyfix.client.util.Util;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity {

    private TextView mTitleView;
    private WebView mDescriptionView;
    private Settings settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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
                .setText("Acerca De");

        ((TextView)findViewById(R.id.txt_version)).setText("Versión " + BuildConfig.VERSION_NAME);
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
}
