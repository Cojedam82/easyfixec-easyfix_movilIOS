package com.easyfixapp.easyfix.models;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.util.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jrealpe on 30/05/17.
 */

public class Service implements Serializable {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("image")
    private String image;

    @Expose
    @SerializedName("is_parent")
    private boolean isParent;

    @Expose
    @SerializedName("sub_services")
    private List<Service> subServiceList;

    @Expose
    @SerializedName("artifacts")
    private List<Artifact> artifactList;

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
        return Util.getUrl(image);
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

    public List<Service> getSubServiceList() {
        return subServiceList;
    }

    public void setSubServiceList(List<Service> subServiceList) {
        this.subServiceList = subServiceList;
    }

    public List<Artifact> getArtifactList() {
        return artifactList;
    }

    public void setArtifactList(List<Artifact> artifactList) {
        this.artifactList = artifactList;
    }

    public int getImageDrawable() {
        switch (id) {
            case 1:
                return R.drawable.service_1;
            case 2:
                return R.drawable.service_2;
            case 3:
                return R.drawable.service_3;
            case 4:
                return R.drawable.service_4;
            case 5:
                return R.drawable.service_5;
            case 6:
                return R.drawable.service_6;
            case 7:
                return R.drawable.service_7;
            case 8:
                return R.drawable.service_8;
            case 9:
                return R.drawable.service_9;
            case 10:
                return R.drawable.service_10;
            case 11:
                return R.drawable.service_11;
            case 12:
                return R.drawable.service_12;
            case 13:
                return R.drawable.service_13;
            default:
                return R.drawable.ic_settings;

        }

    }
}
