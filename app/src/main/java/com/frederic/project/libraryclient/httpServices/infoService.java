package com.frederic.project.libraryclient.httpServices;

import com.frederic.project.libraryclient.models.Administrator;
import com.frederic.project.libraryclient.models.Book;
import com.frederic.project.libraryclient.models.Reader;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by fk on 17-9-19.
 */

public interface infoService {

    @POST("info/reader/changepassword/{id}")
    Call<Integer> changeReaderPassword(@Path("id")int id, @Body RequestBody body);

    @POST("/info/reader/changeinfo/{id}")
    Call<Integer> changeReaderInfo(@Path("id")int id, @Body Reader reader);

    @POST("/info/admin/changepassword/{id}")
    Call<Integer> changeAdminPassword(@Path("id")int id, @Body RequestBody body);

    @POST("/info/admin/changeinfo/{id}")
    Call<Integer> changeAdminInfo(@Path("id")int id, @Body Administrator admin);

    @GET("/info/query/reader/{id}")
    Call<Reader> queryReader(@Path("id")int id);

    @GET("/info/query/book/{isbn}")
    Call<Book> queryBook(@Path("isbn")int isbn);
}