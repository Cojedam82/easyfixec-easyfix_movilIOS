package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserFacebook implements Serializable{

    public UserFacebook(){}
//    @Body("userFacebook") String first_name,
//    @Query("last_name") String last_name,
//    @Query("password") String password,
//    @Query("email") String email,
//    @Query("username") String username,
//    @Query("profile") RequestBody profile,
//    @Query("social") RequestBody social);

    @Expose
    @SerializedName("first_name")
    private String first_name;

    @Expose
    @SerializedName("last_name")
    private String last_name;

    @Expose
    @SerializedName("password")
    private String password;

    @Expose
    @SerializedName("email")
    private String email;

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("profile")
    private ProfileInner profile;

    @Expose
    @SerializedName("social")
    private SocialInner social;

    private class SocialInner{
        @Expose
        @SerializedName("social_id")
        private String social_id;
        @Expose
        @SerializedName("social_token")
        private String social_token;

        public String getSocial_id() {
            return social_id;
        }

        public void setSocial_id(String social_id) {
            this.social_id = social_id;
        }

        public String getSocial_token() {
            return social_token;
        }

        public void setSocial_token(String social_token) {
            this.social_token = social_token;
        }
    }

    public SocialInner createSocial(String social_id, String social_token){
        SocialInner socialProfile= new SocialInner();
        socialProfile.setSocial_id(social_id);
        socialProfile.setSocial_token(social_token);
        return socialProfile;
    }

    public ProfileInner createProfile(String phone){
        ProfileInner profileInner= new ProfileInner();
        profileInner.setPhone(phone);
        return profileInner;
    }

    private class ProfileInner{
        @Expose
        @SerializedName("phone")
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ProfileInner getProfile() {
        return profile;
    }

    public void setProfile(ProfileInner profile) {
        this.profile = profile;
    }

    public SocialInner getSocial() {
        return social;
    }

    public void setSocial(SocialInner social) {
        this.social = social;
    }
}
