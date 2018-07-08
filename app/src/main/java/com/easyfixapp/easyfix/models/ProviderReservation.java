package com.easyfixapp.easyfix.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jrealpe on 5/30/18.
 */

public class ProviderReservation implements Parcelable {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("reservation")
    private Reservation reservation;

    @Expose
    @SerializedName("provider")
    private User provider;

    @Expose
    @SerializedName("time")
    private Long time;

    public ProviderReservation() {}

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ProviderReservation createFromParcel(Parcel in) {
            return new ProviderReservation(in);
        }

        public ProviderReservation[] newArray(int size) {
            return new ProviderReservation[size];
        }
    };

    public ProviderReservation(Parcel in) {
        this.id = in.readInt();

        this.reservation = in.readParcelable(Reservation.class.getClassLoader());
        this.provider = in.readParcelable(User.class.getClassLoader());

        long tmpTime = in.readLong();
        this.time = tmpTime == -1 ? null : tmpTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getProvider() {
        return provider;
    }

    public void setProvider(User provider) {
        this.provider = provider;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);

        dest.writeParcelable(this.reservation, flags);
        dest.writeParcelable(this.provider, flags);

        dest.writeLong(this.time != null? this.time : -1);
    }
}
