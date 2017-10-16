package com.easyfixapp.easyfix.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.util.SessionManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }


    /**
     * Initialize user and actions
     */
    public void initialize(){

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(SplashActivity.this);
                String token = sessionManager.getToken();
                Intent intent;

                if (sessionManager.hasToken()) intent = new Intent(SplashActivity.this, MainActivity.class);
                else intent = new Intent(SplashActivity.this, LoginActivity.class);

                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}
