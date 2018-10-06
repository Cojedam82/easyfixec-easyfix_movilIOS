package com.easyfix.client.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by julio on 6/11/17.
 */

public class Address extends RealmObject implements Parcelable {

    @Expose
    @SerializedName("id")
    @PrimaryKey
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("reference")
    private String reference;

    @Expose
    @SerializedName("latitude")
    private Double latitude;

    @Expose
    @SerializedName("longitude")
    private Double longitude;

    @Expose
    @SerializedName("is_active")
    private boolean isActive;

    @Expose
    @SerializedName("is_default")
    private boolean isDefault;

    public Address() {
        this.isActive = false;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public Address(Parcel in) {
        this.id = in.readInt();

        this.name = in.readString();
        this.description = in.readString();
        this.reference = in.readString();

        this.latitude = in.readDouble();
        this.longitude = in.readDouble();

        this.isActive = in.readByte() != 0;
        this.isDefault = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);

        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.reference);

        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);

        dest.writeByte((byte) (this.isActive ? 1 : 0));
        dest.writeByte((byte) (this.isDefault ? 1 : 0));
    }
}
