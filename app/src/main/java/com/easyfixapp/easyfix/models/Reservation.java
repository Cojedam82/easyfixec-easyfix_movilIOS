package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;

/**
 * Created by julio on 11/06/17.
 */

public class Reservation implements Serializable {

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
    @SerializedName("type")
    private String type;

    @Expose
    @SerializedName("time")
    private String time;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("cost")
    private String cost;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("artifact")
    private String artifact;

    @Expose
    @SerializedName("address")
    private Address address;

    @Expose
    @SerializedName("service")
    private Service service;

    @Expose
    @SerializedName("client")
    private User client;

    @Expose
    @SerializedName("provider")
    private User provider;

    @Expose(serialize = false)
    @SerializedName("image1")
    private String image1;

    @Expose(serialize = false)
    @SerializedName("image2")
    private String image2;

    @Expose(serialize = false)
    @SerializedName("image3")
    private String image3;

    @Expose(serialize = false)
    @SerializedName("image4")
    private String image4;

    private byte[][] imageByteList;

    private File[] imageFileList;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
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

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public byte[][] getImageByteList() {
        return imageByteList;
    }

    public void setImageByteList(byte[][] imageByteList) {
        this.imageByteList = imageByteList;
    }

    public File[] getImageFileList() {
        return imageFileList;
    }

    public void setImageFileList(File[] imageFileList) {
        this.imageFileList = imageFileList;
    }
}
