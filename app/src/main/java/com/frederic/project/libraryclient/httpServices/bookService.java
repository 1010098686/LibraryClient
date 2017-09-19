package com.frederic.project.libraryclient.httpServices;

import com.frederic.project.libraryclient.models.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fk on 17-9-18.
 */

public interface bookService {

    @GET("/book/borrow")
    Call<Integer> borrowBook(@Query("id")int id,@Query("isbn")int isbn);

    @GET("/book/return")
    Call<Double> returnBook(@Query("id")int id, @Query("isbn")int isbn);

    @GET("/book/query")
    Call<List<Book>> query(@Query("queryString")String queryString);

    @GET("/book/reborrow")
    Call<Integer> reborrowBook(@Query("id")int id, @Query("isbn")int isbn);

    @PUT("/book/modify/{isbn}")
    Call<Integer> modifyBook(@Path("isbn")int isbn, @Body Book book);

    @DELETE("/book/delete/{isbn}")
    Call<Integer> deleteBook(@Path("isbn")int isbn);

    @POST("/book/add")
    Call<Integer> addBook(@Body Book book);

}
