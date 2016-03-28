package com.example.jessicaz.readbook.AsyncTack;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jessicaz.readbook.model.Book;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jessicazeng on 3/24/16.
 */
public class GetHtmlContentRemoteAsyncTask extends AsyncTask<Void, Void, File> {
    private Book book;
    private Context context;
    GetHtmlContentRemote getHtmlContentRemote;
    File file;

    public interface GetHtmlContentRemote {
        void onContentReceived(File file, Book book);
    }

    public GetHtmlContentRemoteAsyncTask(Context context, GetHtmlContentRemote getHtmlContentRemote, Book book) {
        this.getHtmlContentRemote = getHtmlContentRemote;
        this.book = book;
        this.context = context;
    }

    @Override
    protected File doInBackground(Void... params) {
        URL url;
        HttpURLConnection connection = null;
        StringBuilder html = new StringBuilder("");

        try {
            url = new URL(book.getBookURL());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int status = connection.getResponseCode();
            Log.w("status", String.valueOf(status));

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                html.append(line);
            }

            String fileName = book.getBookName().trim();

            file = File.createTempFile(fileName, ".html", context.getCacheDir());

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(html.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        return file;
    }

    @Override
    protected void onPostExecute(File result) {
        getHtmlContentRemote.onContentReceived(result, book);
    }
}
