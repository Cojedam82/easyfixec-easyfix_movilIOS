package com.easyfixapp.easyfix.models;

/**
 * Created by julio on 16/05/17.
 */

public class User {
    private int id;
    private String first_name, last_name, email, password;
    private Profile profile;

    public User(){};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getShortName() {
        return getFirstName().split(" ")[0] + ' ' + getLastName().split(" ")[0];
    }

    public String getFullName() {
        return getFirstName() + ' ' + getLastName();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
