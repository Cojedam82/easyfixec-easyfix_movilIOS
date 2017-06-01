package com.easyfixapp.easyfix.models;

/**
 * Created by julio on 29/05/17.
 */

public class Profile {
    private int role;
    private String phone, token;

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
}
