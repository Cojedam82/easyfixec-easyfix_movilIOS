package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by julio on 11/06/17.
 */

public class Reservation {

    // To item
    public static final int TYPE_NOTIFICATION = 0;
    public static final int TYPE_RECORD = 1;

    // To status
    public static final int  Assigned = 1;
    public static final int  Canceled = 2;
    public static final int  Pending = 3;
    public static final int  Unrealized = 4;
    public static final int  Done = 5;

    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private int status;

    @SerializedName("time")
    private String time;

    @SerializedName("date")
    private String date;

    @SerializedName("cost")
    private String cost;

    @SerializedName("service")
    private Service service;

    @SerializedName("client")
    private User client;

    @SerializedName("provider")
    private User provider;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getProvider() {
        return provider;
    }

    public void setProvider(User provider) {
        this.provider = provider;
    }
}
