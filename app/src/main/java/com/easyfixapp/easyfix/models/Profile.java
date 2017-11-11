package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by julio on 29/05/17.
 */

public class Profile extends RealmObject{

    @SerializedName("role")
    private int role;

    @SerializedName("phone")
    private String phone;

    @SerializedName("token")
    private String token;

    @SerializedName("email")
    private String image;

    public Profile(){};

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
