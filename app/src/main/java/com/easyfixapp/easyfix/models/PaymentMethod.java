package com.easyfixapp.easyfix.models;


import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class PaymentMethod implements Serializable {

    @Expose
    private String name;

    @Expose
    private int id;

    public PaymentMethod(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}