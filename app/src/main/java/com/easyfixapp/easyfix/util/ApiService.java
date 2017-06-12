package com.easyfixapp.easyfix.util;

import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

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
    @GET("reservations/?status=3")
    Call<List<Reservation>> getAgenda(@Header("Authorization") String authorization);

    @GET("reservations/?status=5")
    Call<List<Reservation>> getRecord(@Header("Authorization") String authorization);
}

