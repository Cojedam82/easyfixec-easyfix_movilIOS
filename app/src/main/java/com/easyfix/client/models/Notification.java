package com.easyfix.client.models;

/**
 * Created by jrealpe on 5/30/18.
 */

public class Notification {
    private int action;
    private Object object;

    public Notification(int action) {
        this.action = action;
    }

    public Notification(int action, Object object) {
        this.action = action;
        this.object = object;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
