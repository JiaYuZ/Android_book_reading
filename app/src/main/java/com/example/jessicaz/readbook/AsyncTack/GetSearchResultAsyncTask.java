package com.example.jessicaz.readbook.AsyncTack;

import android.os.AsyncTask;
import android.speech.tts.Voice;

import com.example.jessicaz.readbook.BuildConfig;
import com.example.jessicaz.readbook.model.Book;
import com.example.jessicaz.readbook.service.BookService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by jessicazeng on 3/25/16.
 */
public class GetSearchResultAsyncTask extends AsyncTask<Void, Void, List<Book>>{
    BookService service;
    GetSearchResult getSearchResult;
    String query;

    public interface GetSearchResult {
        void onResultReceived(List<Book> bookList);
    }

    public GetSearchResultAsyncTask(GetSearchResult getSearchResult, String query) {
        this.getSearchResult = getSearchResult;
        this.query = query;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BOOK_SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(BookService.class);
    }

    @Override
    protected List<Book> doInBackground(Void... params) {
        List<Book> bookList = null;
        Call<List<Book>> call = service.search(query);

        try {
            bookList = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    protected void onPostExecute(List<Book> result) {
        getSearchResult.onResultReceived(result);
    }
}
