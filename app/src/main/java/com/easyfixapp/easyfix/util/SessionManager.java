package com.easyfixapp.easyfix.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.models.Profile;
import com.easyfixapp.easyfix.models.User;

import io.realm.Realm;

/**
 * Created by julio on 29/05/17.
 */

public class SessionManager {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Resources mResources;

    public SessionManager(Context mContext){
        this.mContext = mContext;
        this.mSharedPreferences = mContext.getSharedPreferences("EASYFIX",Context.MODE_PRIVATE);
        this.mEditor = mSharedPreferences.edit();
        this. mResources =  mContext.getResources();
    }

    public void saveUser(User user){
        Profile profile = user.getProfile();

        mEditor.putInt(mResources.getString(R.string.preferences_user_id), user.getId());
        mEditor.putString(mResources.getString(R.string.preferences_user_first_name), user.getFirstName());
        mEditor.putString(mResources.getString(R.string.preferences_user_last_name), user.getLastName());
        mEditor.putString(mResources.getString(R.string.preferences_user_email), user.getEmail());

        try {
            mEditor.putString(mResources.getString(R.string.preferences_profile_image), profile.getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mEditor.putString(mResources.getString(R.string.preferences_profile_phone), profile.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mEditor.putString(mResources.getString(R.string.preferences_profile_token), profile.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mEditor.putInt(mResources.getString(R.string.preferences_profile_role), profile.getRole());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mEditor.putInt(mResources.getString(R.string.preferences_profile_payment_method), profile.getPaymentMethod());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEditor.apply();
try{
        if (!user.getAddresses().isEmpty()) {
            Realm realm = Realm.getDefaultInstance();
            try {
                realm.beginTransaction();
                realm.copyToRealm(user.getAddresses());
                realm.commitTransaction();
            } catch (Exception ignore){

            } finally {
                realm.close();
            }

        } }catch(Exception e){}
    }

    public User getUser(){
        User user = new User();
        Profile profile = new Profile();

        user.setId(mSharedPreferences.getInt(mResources.getString(R.string.preferences_user_id), -1));
        user.setFirstName(mSharedPreferences.getString(mResources.getString(R.string.preferences_user_first_name), null));
        user.setLastName(mSharedPreferences.getString(mResources.getString(R.string.preferences_user_last_name), null));
        user.setEmail(mSharedPreferences.getString(mResources.getString(R.string.preferences_user_email), null));

        profile.setImage(mSharedPreferences.getString(mResources.getString(R.string.preferences_profile_image), null));
        profile.setPhone(mSharedPreferences.getString(mResources.getString(R.string.preferences_profile_phone), null));
        profile.setToken(mSharedPreferences.getString(mResources.getString(R.string.preferences_profile_token), null));
        profile.setRole(mSharedPreferences.getInt(mResources.getString(R.string.preferences_profile_role), -1));
        profile.setPaymentMethod(mSharedPreferences.getInt(mResources.getString(R.string.preferences_profile_payment_method), 0));
        user.setProfile(profile);

        return user;
    }

    public boolean hasToken(){
        return !TextUtils.isEmpty(mSharedPreferences.getString(mResources.getString(R.string.preferences_profile_token), null));
    }

    public String getToken(){
        return "Token " + mSharedPreferences.getString(mResources.getString(R.string.preferences_profile_token), null);
    }

    public int getFragments() {
        return mSharedPreferences.getInt(mResources.getString(R.string.preferences_number_fragment), 0);
    }

    public void addFragment() {
        mEditor.putInt(mResources.getString(R.string.preferences_number_fragment), getFragments() + 1);
        mEditor.apply();
    }

    public void resetFragment() {
        mEditor.putInt(mResources.getString(R.string.preferences_number_fragment), 0);
        mEditor.apply();
    }

    public int getServiceDetail() {
        return mSharedPreferences.getInt(mResources.getString(R.string.preferences_service_detail), 0);
    }

    public void setServiceDetail(int id) {
        mEditor.putInt(mResources.getString(R.string.preferences_service_detail), id);
        mEditor.apply();
    }

    public void resetServiceDetail() {
        mEditor.putInt(mResources.getString(R.string.preferences_service_detail), 0);
        mEditor.apply();
    }

    public void clear(){
        mEditor.clear();
        mEditor.apply();
    }
}