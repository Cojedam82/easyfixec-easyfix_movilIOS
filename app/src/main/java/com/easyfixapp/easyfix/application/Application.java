package com.easyfixapp.easyfix.application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application{

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        Application.application = this;

        // Fabric
        Fabric.with(this, new Crashlytics());
    }

}
