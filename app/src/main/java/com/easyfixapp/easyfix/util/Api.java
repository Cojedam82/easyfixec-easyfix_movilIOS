package com.easyfixapp.easyfix.util;

/**
 * Created by julio on 22/02/17.
 */
public interface Api {

    /**
     * User
     */
    /*
    @POST("users/")
    Call<User> signUp(@Body User user);

    @FormUrlEncoded
    @POST("users/login/")
    Call<UserResponse<User>> signIn(@FieldMap Map<String, String> params);
*/

    /*

    @FormUrlEncoded
    @POST("users/recoveryPassword/")
    Call<Void> recoveryPassword(@Field("email") String email);

    @PUT
    Call<User> updateProfile(@Url String url, @Header("Authorization") String authorization, @Body User user);

    @Multipart
    @POST("users/updateImage/")
    Call<User> updateImageProfile(@Header("Authorization") String authorization, @Part MultipartBody.Part image);

    @GET("users/user/")
    Call<User> getUser(@Header("Authorization") String authorization);

    /**
     * Training
     *

    @GET("trainings/")
    Call<List<Training>> getTraining(@Header("Authorization") String authorization);

    /**
     * Fcm device
     *

    @POST("devices/")
    Call<FCM> registerDevice(@Header("Authorization") String authorization, @Body FCM FCM);

    /**
     * Payments
     *

    @GET
    Call<PaymentCode> getPaymentCode(@Url String url, @Header("Authorization") String authorization);

    @POST("memberships/payment/")
    Call<MembershipResponse<Membership>> confirmPayment(@Header("Authorization") String authorization, @Header("X-Code") String code, @Body UserMembership userMembership);

    /**
     * Membership
     *
    @GET("memberships/")
    Call<List<Membership>> getMemberships(@Header("Authorization") String authorization);

    /**
     * FAQ
     *
    @GET("faqs/")
    Call<List<FAQ>> getFAQs(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("faqs/")
    Call<Void> postFAQ(@Header("Authorization") String authorization, @Field("question") String question);

    /**
     * Record
     *
    @GET("records/user/")
    Call<List<UserHistory>> getRecord(@Header("Authorization") String authorization);


    @POST("records/")
    Call<UserHistory> postRecord(@Header("Authorization") String authorization, @Body UserHistory userHistory);

    /**
     * Tip
     *
    @GET("tips/")
    Call<List<Tip>> getTip(@Header("Authorization") String authorization);


    /**
     * Intro
     *
    @GET("intro/")
    Call<List<Intro>> getIntro(@Header("Authorization") String authorization);
    */
}

