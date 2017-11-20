package com.easyfixapp.easyfix.util;

import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by julio on 22/05/17.
 */
public interface ApiService {

    /** Services **/
    @GET("services/")
    Call<List<Service>> getServices(@Header("Authorization") String authorization);


    /**
     *  Reservations
     *      1 --> Asignada
     *      2 --> Cancelada
     *      3 --> Pendiente
     *      4 --> No Realizada
     *      5 --> Realizada
     **/
    @GET("reservations/?status__in=1,3")
    Call<List<Reservation>> getNotifications(@Header("Authorization") String authorization);

    @GET("reservations/?status__in=5")
    Call<List<Reservation>> getRecord(@Header("Authorization") String authorization);

    @DELETE("reservations/{pk}/")
    Call<Reservation> deleteReservation(@Path("pk") int pk, @Header("Authorization") String authorization);


    /** Address **/
    @POST("addresses/")
    Call<Address> createAddress(@Header("Authorization") String authorization);

    @PATCH("addresses/{pk}/")
    Call<Void> updateAddress(@Path("pk") int pk, @Header("Authorization") String authorization);

    @DELETE("addresses/{pk}/")
    Call<Void> deleteAddress(@Path("pk") int pk, @Header("Authorization") String authorization);


    /** User **/
    @PATCH("users/{pk}/")
    Call<User> updateUser(@Path("pk") int pk,
                          @Header("Authorization") String authorization,
                          @Body User user);

}

