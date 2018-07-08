package com.easyfixapp.easyfix.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.annotations.Ignore;

/**
 * Created by julio on 16/05/17.
 */

public class User implements Parcelable {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("first_name")
    private String firstName;

    @Expose
    @SerializedName("last_name")
    private String lastName;

    @Expose
    @SerializedName("email")
    private String email;

    @Expose(deserialize = false)
    @Ignore
    private String password;

    @Expose
    @SerializedName("profile")
    private Profile profile;

    @Expose
    @SerializedName("addresses")
    private RealmList<Address> addresses;

    public User(){};

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in) {
        this.id = in.readInt();

        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.password = in.readString();

        this.addresses = new RealmList<>();
        this.addresses.addAll(in.createTypedArrayList(User.CREATOR));
    }

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

    public float getScore() {
        float score = 0;

        try {
            score = getProfile().getScore();
        } catch (Exception ignore) {}

        return score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);

        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.password);

        dest.writeTypedList(this.addresses);
    }
}
