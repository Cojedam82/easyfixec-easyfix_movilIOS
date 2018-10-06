package com.easyfix.client.models;

import android.content.Context;

import com.easyfix.client.R;
import com.easyfix.client.util.Util;

import java.io.Serializable;

/**
 * Created by julio on 4/11/17.
 */

public class Settings implements Serializable {

    private String name;
    private String url;

    private static Settings instance = null;

    Settings (){
        this.name = "";
        this.url = "";
    }

    public static Settings newInstance() {

        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    public static Settings createPolicies(Context context) {
        instance = newInstance();
        instance.setName(context.getString(R.string.setting_policies));
        instance.setUrl(Util.PRIVACY_POLICIES_URL);
        return instance;
    }

    public static Settings createTerms(Context context) {
        instance = newInstance();
        instance.setName(context.getString(R.string.setting_terms));
        instance.setUrl(Util.TERMS_URL);
        return instance;
    }

    public static Settings createHelp(Context context) {
        instance = newInstance();
        instance.setName(context.getString(R.string.setting_help));
        instance.setUrl(Util.HELP_URL);
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
