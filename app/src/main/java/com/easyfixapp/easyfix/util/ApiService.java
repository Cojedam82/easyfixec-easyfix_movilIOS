package com.easyfixapp.easyfix.util;

import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.models.AuthResponse;
import com.easyfixapp.easyfix.models.Reservation;
import com.easyfixapp.easyfix.models.Service;
import com.easyfixapp.easyfix.models.User;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.QueryName;

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
    @Multipart
    @POST("reservations/")
    Call<Reservation> createReservation(
            @Header("Authorization") String authorization,
            @PartMap() Map<String, RequestBody> params,
            @Part MultipartBody.Part image1,
            @Part MultipartBody.Part image2,
            @Part MultipartBody.Part image3,
            @Part MultipartBody.Part image4);

    @GET("reservations/?status__in=1,3")
    Call<List<Reservation>> getNotifications(@Header("Authorization") String authorization);

    @GET("reservations/?status__in=4,5")
    Call<List<Reservation>> getRecord(@Header("Authorization") String authorization);

    @DELETE("reservations/{pk}/")
    Call<Void> deleteReservation(@Path("pk") int pk, @Header("Authorization") String authorization);


    /** Address **/
    @POST("addresses/")
    Call<Address> createAddress(@Header("Authorization") String authorization, @Body Address address);
//FACEBOOK INTEGRATION:
    @POST("signupsocial2/")
    Call<com.google.gson.JsonObject> signUpSocial(@Query("first_name") String first_name,
                                  @Query("last_name") String last_name,
                                  @Query("password") String password,
                                  @Query("email") String email,
                                  @Query("username") String username,
                                  @Query("profile") RequestBody profile,
                                  @Query("social") RequestBody social);
//HASTA ACA ES FB
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

