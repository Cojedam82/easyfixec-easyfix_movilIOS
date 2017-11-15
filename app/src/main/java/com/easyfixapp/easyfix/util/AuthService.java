package com.easyfixapp.easyfix.util;

import com.easyfixapp.easyfix.models.AuthResponse;
import com.easyfixapp.easyfix.models.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by julio on 22/05/17.
 */
public interface AuthService {

    /**
     * Auth
     */

    @FormUrlEncoded
    @POST("login/")
    Call<AuthResponse<User>> login(@FieldMap Map<String, Object> params);

    @POST("signup/")
    Call<AuthResponse<User>> signup(@Body User user);
}

