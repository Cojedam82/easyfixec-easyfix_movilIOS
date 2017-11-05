package com.easyfixapp.easyfix.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by julio on 23/10/17.
 */

public class Artifact implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

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

    @Override
    public String toString() {
        return getName();
    }
}
