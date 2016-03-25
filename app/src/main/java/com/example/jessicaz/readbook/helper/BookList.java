package com.example.jessicaz.readbook.helper;

import android.provider.BaseColumns;

/**
 * Created by jessicazeng on 3/23/16.
 */
public class BookList {
    private BookList() {

    }

    public static abstract class BookEntry implements BaseColumns {
        public static final String TABLE_BOOKS = "booktable";
        public static final String ROW_ID = "id";
        public static final String ROW_BOOK_ID = "bookid";
        public static final String ROW_BOOK_NAME = "bookname";
        public static final String ROW_BOOK_URL = "bookurl";
        public static final String ROW_AUTHOR_NAME = "authorname";
        public static final String ROW_BOOK_IMAGE_URL = "imageurl";
        public static final String ROW_BOOK_PATH = "bookpath";
        public static final String ROW_BOOK_VISIT_COUNT = "visitcount";
    }
}
