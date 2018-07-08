package com.easyfixapp.easyfix.util;

import com.easyfixapp.easyfix.parser.DateTypeParser;
import com.easyfixapp.easyfix.parser.TimeTypeParser;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by julio on 30/05/17.
 */

public class ServiceGenerator {

    /**
     * Build simple REST adapter
     */

    private static GsonConverterFactory gsonConverterFactory = GsonConverterFactory
            .create(new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Date.class, new DateTypeParser())
                    .registerTypeAdapter(Long.class, new TimeTypeParser())
                    .create());

    private static Retrofit.Builder mRetrofitBuilder = new Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory);

    public static ApiService createApiService() {
        // Create an instance of our Object API interface.
        return  mRetrofitBuilder.baseUrl(Util.API_URL).build().create(ApiService.class);
    }

    public static AuthService createAuthService() {
        // Create an instance of our Object API interface.
        return  mRetrofitBuilder.baseUrl(Util.BASE_URL).build().create(AuthService.class);
    }
}
