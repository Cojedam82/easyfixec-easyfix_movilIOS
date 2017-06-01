package com.easyfixapp.easyfix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.User;
import com.easyfixapp.easyfix.util.SessionManager;


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

                if (TextUtils.isEmpty(token)) intent = new Intent(SplashActivity.this, LoginActivity.class);
                else intent = new Intent(SplashActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}
