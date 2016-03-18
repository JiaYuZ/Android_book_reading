package com.example.jessicaz.readbook.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jessicaz.readbook.model.Book;

import java.util.List;

/**
 * Created by jessicazeng on 10/27/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BookDB";

    private static final String TABLE_BOOKS = "books";
    private static final String KEY_ID = "id";
    private static final String KEY_BOOKNAME = "bookName";
    private static final String KEY_STARS = "stars";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BOOKNAME + " TEXT," + KEY_STARS + " INTEGER" + ")";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }

    private static final String[] COLUMNS = {KEY_ID, KEY_BOOKNAME, KEY_STARS};

    public void addStar(Book book) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_BOOKNAME, book.getBookName());
        values.put(KEY_STARS, book.getStars());

        db.insert(TABLE_BOOKS, null, values);
        db.close();
    }

    public Book getStar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, new String[] { KEY_ID,
                        KEY_BOOKNAME, KEY_STARS }, KEY_ID + " =? ",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Book book = new Book(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getInt(2));
        // return book
        cursor.close();
        db.close();

        return book;
    }

    public int updateStar(Book book) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOKNAME, book.getBookName());
        values.put(KEY_STARS, book.getStars());

        int i = db.update(TABLE_BOOKS, values, KEY_ID + " = ? ",
                new String[] {String.valueOf(book.getId())});

        db.close();

        return i;
    }

    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BOOKS, KEY_ID + " = ? ", new String[]{String.valueOf(book.getId())});

        db.close();

        Log.d("delete", book.toString());
    }
}
