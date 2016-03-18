package com.example.jessicaz.readbook.Interface;

import com.example.jessicaz.readbook.model.Book;

import java.util.List;

/**
 * Created by jessicazeng on 10/9/15.
 */
public interface GetBooksRemote{
    void onBooksReceived(List<Book> bookList);

}