package com.easyfix.client.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by julio on 11/06/17.
 */

public class Reservation implements Parcelable {

    // To item
    public static final int TYPE_NOTIFICATION = 0;
    public static final int TYPE_RECORD = 1;

    // To status
    public static final int  Assigned = 1;
    public static final int  Canceled = 2;
    public static final int  Pending = 3;
    public static final int  Unrealized = 4;
    public static final int  Done = 5;

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("status")
    private int status;

    @Expose
    @SerializedName("cost")
    private float cost;

    @Expose
    @SerializedName("description")
    private String description;

    //@Expose
    //@SerializedName("type")
    //private String type;

    @Expose
    @SerializedName("time")
    private Long time;

    @Expose
    @SerializedName("date")
    private Date date;

    //@Expose
    //@SerializedName("artifact")
    //private String artifact;

    @Expose
    @SerializedName("address")
    private Address address;

    @Expose
    @SerializedName("service")
    private Service service;

    //@Expose
    //@SerializedName("client")
    //private User client;

    @Expose
    @SerializedName("provider")
    private User provider;

    //@Expose(serialize = false)
    //@SerializedName("image1")
    //private String image1;

    //@Expose(serialize = false)
    //@SerializedName("image2")
    //private String image2;

    //@Expose(serialize = false)
    //@SerializedName("image3")
    //private String image3;

    //@Expose(serialize = false)
    //@SerializedName("image4")
    //private String image4;

    //private byte[][] imageByteList;

    //private File[] imageFileList;

    @Expose
    @SerializedName("is_scheduled")
    private boolean isScheduled;

    public Reservation() {
        this.description = "";
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    public Reservation(Parcel in) {
        this.id = in.readInt();
        this.status = in.readInt();

        this.cost = in.readFloat();

        this.description = in.readString();

        long tmpTime = in.readLong();
        this.time = tmpTime == -1 ? null : tmpTime;

        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);

        this.address = in.readParcelable(Address.class.getClassLoader());
        this.service = in.readParcelable(Service.class.getClassLoader());
        this.provider = in.readParcelable(User.class.getClassLoader());

        this.isScheduled = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getProvider() {
        return provider;
    }

    public void setProvider(User provider) {
        this.provider = provider;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.status);

        dest.writeFloat(this.cost);

        dest.writeString(this.description);

        dest.writeLong(this.time != null? this.time : -1);
        dest.writeLong(this.date != null? this.date.getTime() : -1);

        dest.writeParcelable(this.address, flags);
        dest.writeParcelable(this.service, flags);
        dest.writeParcelable(this.provider, flags);

        dest.writeByte((byte) (this.isScheduled ? 1 : 0));
    }
}
