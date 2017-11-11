package com.easyfixapp.easyfix.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.easyfixapp.easyfix.R;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Application extends android.app.Application{

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        Application.application = this;

        // Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(getString(R.string.app_name).toLowerCase() + ".realm")
                //.encryptionKey(getKey())
                .schemaVersion(1)
                //.modules(new MySchemaModule())
                //.migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(config);

        // Fabric
        Fabric.with(this, new Crashlytics());

        // Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AntipastoRegular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
