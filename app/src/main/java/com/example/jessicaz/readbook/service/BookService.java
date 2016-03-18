package com.example.jessicaz.readbook.service;

import android.support.annotation.RequiresPermission;

import com.example.jessicaz.readbook.model.Book;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by jessicazeng on 9/30/15.
 */
public interface BookService {

    @GET("/books")
    Call<List<Book>> getBooks();

    @FormUrlEncoded
    @POST("/login")
    Call<String> login(@Field("username") String username, @Field("password") String password);
}
