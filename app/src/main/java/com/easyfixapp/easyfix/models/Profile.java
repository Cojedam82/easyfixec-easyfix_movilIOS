package com.easyfixapp.easyfix.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.easyfixapp.easyfix.util.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by julio on 29/05/17.
 */

public class Profile extends RealmObject implements Parcelable {

    @Expose
    @SerializedName("role")
    private int role;

    @Expose
    @SerializedName("phone")
    private String phone;

    @Expose(serialize = false)
    @SerializedName("token")
    private String token;

    @Expose(serialize = false)
    @SerializedName("image")
    private String image;

    @Expose
    @SerializedName("payment_method")
    private int paymentMethod;

    @Expose
    @SerializedName("score")
    private float score;

    public static final int CASH_KEY = 1;
    public static final String CASH_VALUE = "Efectivo";

    public static final int CREDIT_CARD_KEY = 2;
    public static final String CREDIT_CARD_VALUE = "Tarjeta de crédito";

    public Profile(){};

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public Profile(Parcel in) {
        this.role = in.readInt();

        this.phone = in.readString();
        this.token = in.readString();
        this.image = in.readString();

        this.paymentMethod = in.readInt();

        this.score = in.readFloat();
    }

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
        return Util.getUrl(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getPaymentMethodString() {
        return this.paymentMethod == CASH_KEY? CASH_VALUE : CREDIT_CARD_VALUE;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.role);

        dest.writeString(this.phone);
        dest.writeString(this.token);
        dest.writeString(this.image);

        dest.writeInt(this.paymentMethod);

        dest.writeFloat(this.score);
    }
}
