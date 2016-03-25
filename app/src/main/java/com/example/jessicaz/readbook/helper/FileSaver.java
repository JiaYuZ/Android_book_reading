package com.example.jessicaz.readbook.helper;

import android.content.Context;

import com.example.jessicaz.readbook.AsyncTack.GetHtmlContentRemoteAsyncTask;
import com.example.jessicaz.readbook.model.Book;

/**
 * Created by jessicazeng on 3/24/16.
 */
public class FileSaver {
    private Context context;
    private Book book;
    GetHtmlContentRemoteAsyncTask.GetHtmlContentRemote getHtmlContentRemote;

    public FileSaver (Context context, Book book, GetHtmlContentRemoteAsyncTask.GetHtmlContentRemote getHtmlContentRemote) {
        this.context = context;
        this.book = book;
        this.getHtmlContentRemote = getHtmlContentRemote;
    }

    public void createFile() {
        GetHtmlContentRemoteAsyncTask getHtmlContentRemoteAsyncTask = new GetHtmlContentRemoteAsyncTask(context, getHtmlContentRemote, book);
        getHtmlContentRemoteAsyncTask.execute();
    }
}
