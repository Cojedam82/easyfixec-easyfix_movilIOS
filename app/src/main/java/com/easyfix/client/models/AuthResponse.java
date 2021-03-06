package com.easyfix.client.models;

import com.google.gson.annotations.Expose;

/**
 * Created by julio on 22/05/17.
 */
public class AuthResponse<T> {

    @Expose
    private boolean error;

    @Expose
    private String msg;

    @Expose
    private T data;

    public AuthResponse(boolean error, String msg){
        this.error = error;
        this.msg = msg;
    }

    public AuthResponse(boolean error, String msg, T data){
        this.error = error;
        this.msg = msg;
        this.data = data;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
