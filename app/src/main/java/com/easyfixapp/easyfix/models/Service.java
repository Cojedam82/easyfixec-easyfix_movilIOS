package com.easyfixapp.easyfix.models;

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
}
