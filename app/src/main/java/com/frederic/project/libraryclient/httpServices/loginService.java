package com.frederic.project.libraryclient.httpServices;

import com.frederic.project.libraryclient.models.Administrator;
import com.frederic.project.libraryclient.models.Reader;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by fk101 on 2017/09/13.
 */

public interface loginService {


    @POST("login/admin")
    Call<Administrator> loginAsAdmin(@Body RequestBody body);


    @POST("login/reader")
    Call<Reader> loginAsReader(@Body RequestBody body);
}
