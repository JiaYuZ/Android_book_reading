package com.example.jessicaz.readbook.service;

import com.example.jessicaz.readbook.model.Book;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by jessicazeng on 9/30/15.
 */
public interface BookService {
    @GET("/books")
    Call<List<Book>> getBooks();

    @FormUrlEncoded
    @POST("/search")
    Call<List<Book>> search(@Field("query") String query);

    @FormUrlEncoded
    @POST("/login")
    Call<String> login(@Field("username") String username, @Field("password") String password);
}
