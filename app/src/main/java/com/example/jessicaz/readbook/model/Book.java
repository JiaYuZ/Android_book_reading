package com.example.jessicaz.readbook.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

import butterknife.Bind;

/**
 * Created by JessicaZ on 9/27/15.
 */
public class Book implements Serializable,JsonDeserializer{
    private int bookId;
    private String bookName;
    private String bookURL;
    private String authorName;
    private String bookImageURL;

    public Book() {

    }

    public int getId() {
        return bookId;
    }
    public void setId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookURL() {
        return bookURL;
    }
    public void setBookURL(String bookURL) { this.bookURL = bookURL;}

    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName){
        this.authorName = authorName;
    }

    public String getBookImageURL(){
        return  bookImageURL;
    }
    public void setBookImageURL(String bookImageURL){
        this.bookImageURL = bookImageURL;
    }

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement content = json.getAsJsonObject().get("content");
        return new Gson().fromJson(content, typeOfT);
    }
}
