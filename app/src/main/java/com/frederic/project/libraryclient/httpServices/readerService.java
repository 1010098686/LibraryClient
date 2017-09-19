package com.frederic.project.libraryclient.httpServices;

import com.frederic.project.libraryclient.models.Book;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by fk101 on 2017/09/13.
 */

public interface readerService {
    @GET("reader/{id}/borrowbooks")
    Call<List<Book>> getBorrowBooks(@Path("id")int id);

    @GET("/reader/{id}/borrowedbooks")
    Call<List<Book>> getBorrowedBooks(@Path("id")int id);
}
