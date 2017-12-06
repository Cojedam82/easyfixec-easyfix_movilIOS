package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.annotations.Ignore;

/**
 * Created by julio on 16/05/17.
 */

public class User implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @Ignore
    private String password;

    @SerializedName("profile")
    private Profile profile;

    @SerializedName("addresses")
    private RealmList<Address> addresses;

    public User(){};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public RealmList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(RealmList<Address> addresses) {
        this.addresses = addresses;
    }
}
