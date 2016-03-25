package com.example.jessicaz.readbook.AsyncTack;

import android.os.AsyncTask;

import com.example.jessicaz.readbook.BuildConfig;
import com.example.jessicaz.readbook.model.Book;
import com.example.jessicaz.readbook.service.BookService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by jessicazeng on 3/23/16.
 */
public class GetBooksRemoteAsyncTack extends AsyncTask<Void, Void, List<Book>> {
    BookService service;
    GetBooksRemote bookListFragment;

    public interface GetBooksRemote{
        void onBooksReceived(List<Book> bookList);
    }

    public GetBooksRemoteAsyncTack(GetBooksRemote bookListFragment){
        this.bookListFragment = bookListFragment;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BOOKS_JSON_OBJECT_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(BookService.class);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Book> doInBackground(Void... args) {
        List<Book> bookList = null;
        Call<List<Book>> call = service.getBooks();

        try {
            bookList = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    //Update UI
    @Override
    protected void onPostExecute(List<Book> result) {
        bookListFragment.onBooksReceived(result);
    }
}
