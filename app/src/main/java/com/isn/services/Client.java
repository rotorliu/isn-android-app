package com.isn.services;

import android.util.Log;

import com.isn.models.IdResult;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.GsonConverterFactory;

public class Client {

    private static Client instance = new Client();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.31.219:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private UserService userService = retrofit.create(UserService.class);

    private Client() {
    }

    public static synchronized Client getInstance() {
        return instance;
    }

    public void requestVerificationCode(String mobile) throws IOException {

        Call<Void> call = userService.requestVerificationCode(mobile);
        call.execute();
    }

    public long register(String mobile, String name, String password, String verificationCode) throws IOException {
        Call<IdResult> call = userService.register(mobile, name, password, verificationCode);
        Response<IdResult> response = call.execute();
        return response.body().getId();
    }

    public long login(String mobile, String password) throws IOException {
        Call<IdResult> call = userService.login(mobile, password);
        Response<IdResult> response = call.execute();
        return response.body().getId();
    }
}
