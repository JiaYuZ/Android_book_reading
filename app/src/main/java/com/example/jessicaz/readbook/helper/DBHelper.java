package com.example.jessicaz.readbook.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jessicaz.readbook.AsyncTack.GetHtmlContentRemoteAsyncTask;
import com.example.jessicaz.readbook.model.Book;

import java.io.File;

/**
 * Created by jessicazeng on 10/27/15.
 */
public class DBHelper extends SQLiteOpenHelper implements GetHtmlContentRemoteAsyncTask.GetHtmlContentRemote {
    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BookDB";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA = ",";

    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + BookList.BookEntry.TABLE_BOOKS +
            "(" + BookList.BookEntry.ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + BookList.BookEntry.ROW_BOOK_ID + INT_TYPE + COMMA
                + BookList.BookEntry.ROW_BOOK_NAME + TEXT_TYPE + COMMA
                + BookList.BookEntry.ROW_BOOK_URL + TEXT_TYPE + COMMA
                + BookList.BookEntry.ROW_AUTHOR_NAME + TEXT_TYPE + COMMA
                + BookList.BookEntry.ROW_BOOK_IMAGE_URL + TEXT_TYPE + COMMA
                + BookList.BookEntry.ROW_BOOK_PATH + TEXT_TYPE + COMMA
                + BookList.BookEntry.ROW_BOOK_VISIT_COUNT + INT_TYPE + ")";
    private static final String DROP_TABLE_BOOKS = "DROP TABLE IF EXISTS " + BookList.BookEntry.TABLE_BOOKS;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_BOOKS);
        onCreate(db);
    }

    private static final String[] COLUMNS = { BookList.BookEntry.ROW_ID, BookList.BookEntry.ROW_BOOK_ID,
            BookList.BookEntry.ROW_BOOK_NAME, BookList.BookEntry.ROW_BOOK_URL, BookList.BookEntry.ROW_AUTHOR_NAME,
            BookList.BookEntry.ROW_BOOK_IMAGE_URL, BookList.BookEntry.ROW_BOOK_PATH, BookList.BookEntry.ROW_BOOK_VISIT_COUNT };

    public void addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookList.BookEntry.ROW_BOOK_ID, book.getId());
        values.put(BookList.BookEntry.ROW_BOOK_NAME, book.getBookName());
        values.put(BookList.BookEntry.ROW_BOOK_URL, book.getBookURL());
        values.put(BookList.BookEntry.ROW_AUTHOR_NAME, book.getAuthorName());
        values.put(BookList.BookEntry.ROW_BOOK_IMAGE_URL, book.getBookImageURL());
        values.put(BookList.BookEntry.ROW_BOOK_VISIT_COUNT, 0);

        db.insert(BookList.BookEntry.TABLE_BOOKS, null, values);
        db.close();
    }

    public Book getBook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Book book = new Book();
        String sortOrder = BookList.BookEntry.ROW_BOOK_VISIT_COUNT + " DESC";

        Cursor cursor = db.query(
                BookList.BookEntry.TABLE_BOOKS,           // The table to query
                COLUMNS,                                  // The columns to return
                BookList.BookEntry.ROW_ID + " =? ",       // The columns for the WHERE clause
                new String[] { String.valueOf(id) },      // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor != null) {
            cursor.moveToFirst();

            book.setId(cursor.getInt(1));
            book.setBookName(cursor.getString(2));
            book.setBookURL(cursor.getString(3));
            book.setAuthorName(cursor.getString(4));
            book.setBookImageURL(cursor.getString(5));

            cursor.close();
        }

        db.close();
        return book;
    }

    public void insertPath(File file, Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        String path = "file://" + file.getPath();

        ContentValues values = new ContentValues();
        values.put(BookList.BookEntry.ROW_BOOK_PATH, path);

        db.update(BookList.BookEntry.TABLE_BOOKS, values, BookList.BookEntry.ROW_BOOK_ID + " = ? ",
                new String[] {String.valueOf(book.getId())});

        db.close();
    }

    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + BookList.BookEntry.TABLE_BOOKS, null);

        if(cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        db.close();
        return count;
    }

    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(BookList.BookEntry.TABLE_BOOKS, BookList.BookEntry.ROW_BOOK_ID + " = ? ",
                new String[]{String.valueOf(book.getId())});

        db.close();
    }

    public boolean checkDatabase() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    public boolean checkTable() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"
                + BookList.BookEntry.TABLE_BOOKS + "'", null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        db.close();
        return false;
    }

    public boolean checkData() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + BookList.BookEntry.TABLE_BOOKS, null);

        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        db.close();
        return false;
    }

    public void openDatabase() {
        File file = context.getDatabasePath(DATABASE_NAME);
        SQLiteDatabase.openDatabase(file.getPath(), null, 0);
    }

    public void createPath(Book book) {
        FileSaver fileSaver = new FileSaver(context,  book, this);
        fileSaver.createFile();
    }

    public boolean hasPath(Book book) {
        SQLiteDatabase db = this.getReadableDatabase();
        String path = null;

        Cursor cursor = db.rawQuery("SELECT " + BookList.BookEntry.ROW_BOOK_PATH + " FROM " + BookList.BookEntry.TABLE_BOOKS +
                " WHERE " + BookList.BookEntry.ROW_BOOK_ID + " =?", new String[]{String.valueOf(book.getId())});

        if(cursor!=null) {
            cursor.moveToFirst();

            path =  cursor.getString(0);
            cursor.close();
        }

        db.close();
        return path != null;
    }

    public String getPath(Book book) {
        SQLiteDatabase db = this.getReadableDatabase();
        String path = null;

        Cursor cursor = db.rawQuery("SELECT " + BookList.BookEntry.ROW_BOOK_PATH + " FROM " + BookList.BookEntry.TABLE_BOOKS +
                " WHERE " + BookList.BookEntry.ROW_BOOK_ID + " =?", new String[]{String.valueOf(book.getId())});

        if(cursor!=null) {
            cursor.moveToFirst();

            path =  cursor.getString(0);
            cursor.close();
        }

        db.close();
        return path;
    }

    public void increaseVisit(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count;

        Cursor cursor = db.rawQuery("SELECT " + BookList.BookEntry.ROW_BOOK_VISIT_COUNT + " FROM " + BookList.BookEntry.TABLE_BOOKS +
                " WHERE " + BookList.BookEntry.ROW_BOOK_ID + " =?", new String[]{String.valueOf(book.getId())});

        if(cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0) + 1;

            ContentValues values = new ContentValues();
            values.put(BookList.BookEntry.ROW_BOOK_VISIT_COUNT, count);

            db.update(BookList.BookEntry.TABLE_BOOKS, values, BookList.BookEntry.ROW_BOOK_ID + " = ? ",
                    new String[] {String.valueOf(book.getId())});

            cursor.close();
        }

        db.close();
    }

    @Override
    public void onContentReceived(File file, Book book) {
        insertPath(file, book);
    }
}
