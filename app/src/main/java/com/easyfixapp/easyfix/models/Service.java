package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cristian on 30/05/17.
 */

public class Service {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    @SerializedName("is_parent")
    private boolean isParent;

    @SerializedName("sub_service")
    private List<SubService> subServiceList;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public List<SubService> getSubServiceList() {
        return subServiceList;
    }

    public void setSubServiceList(List<SubService> subServiceList) {
        this.subServiceList = subServiceList;
    }
}
