package com.easyfixapp.easyfix.application;

import com.crashlytics.android.Crashlytics;
import com.easyfixapp.easyfix.R;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Application extends android.app.Application{

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        Application.application = this;

        // Fabric
        Fabric.with(this, new Crashlytics());

        // Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AntipastoRegular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
