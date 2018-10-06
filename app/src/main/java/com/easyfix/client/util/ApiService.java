package com.easyfix.client.util;

import com.easyfix.client.models.Address;
import com.easyfix.client.models.AuthResponse;
import com.easyfix.client.models.ProviderReservation;
import com.easyfix.client.models.Reservation;
import com.easyfix.client.models.Service;
import com.easyfix.client.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
    @FormUrlEncoded
    @POST("reservations/")
    Call<Reservation> createReservation(
            @Header("Authorization") String authorization,
            @FieldMap Map<String, Object> params);

    @GET("reservations/?status__in=1,3&ordering=date,time")
    Call<List<Reservation>> getNotifications(@Header("Authorization") String authorization);

    @GET("reservations/?status__in=4,5&ordering=-date,-time")
    Call<List<Reservation>> getRecord(@Header("Authorization") String authorization);

    @GET("reservations/{pk}/")
    Call<Reservation> getReservation(@Path("pk") int pk, @Header("Authorization") String authorization);

    @DELETE("reservations/{pk}/")
    Call<Void> deleteReservation(@Path("pk") int pk, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @PATCH("reservations/{pk}/")
    Call<Reservation> updateReservation(@Path("pk") int pk, @Header("Authorization") String authorization,
                                        @FieldMap Map<String, Object> params);

    @GET("reservations/{pk}/providers")
    Call<List<ProviderReservation>> getProviders(@Path("pk") int pk, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("reservations/{pk}/evaluation/")
    Call<Void> createEvaluation(@Path("pk") int pk, @Header("Authorization") String authorization, @Field("score") float score);


    /** Address **/
    @POST("addresses/")
    Call<Address> createAddress(@Header("Authorization") String authorization, @Body Address address);

    @PATCH("addresses/{pk}/")
    Call<Void> updateAddress(@Path("pk") int pk, @Header("Authorization") String authorization);

    @DELETE("addresses/{pk}/")
    Call<Void> deleteAddress(@Path("pk") int pk, @Header("Authorization") String authorization);


    /** User **/
    @FormUrlEncoded
    @POST("users/recovery-password/")
    Call<AuthResponse<Void>> recoveryPassword(@FieldMap Map<String, Object> params);

    @PATCH("users/{pk}/")
    Call<User> updateUser(@Path("pk") int pk,
                          @Header("Authorization") String authorization,
                          @Body User user);

    @Multipart
    @POST("users/update-image/")
    Call<User> updateImageProfile(@Header("Authorization") String authorization, @Part MultipartBody.Part image);
}

