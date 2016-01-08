package com.isn.services;

import com.isn.models.IdResult;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by 刘宝 on 2016/1/6.
 */
public interface UserService {
    @DELETE("users/{userid}")
    Call<Void> delete(@Path("userid") long userid);

//    @GET("users/{userid}")
//    Call<User> get(@Path("userid") long userid);

    @GET("users/requestVerificationCode")
    Call<Void> requestVerificationCode(@Query("mobile") String mobile);

    @POST("users/register")
    Call<IdResult> register(@Query("mobile") String mobile,
                         @Query("name") String name,
                         @Query("password") String password,
                         @Query("verificationCode") String verificationCode);

    @GET("users/login")
    Call<IdResult> login(@Query("mobile") String mobile, @Query("password") String password);


}
