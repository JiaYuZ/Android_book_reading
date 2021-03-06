package com.example.jessicaz.readbook.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.jessicaz.readbook.AsyncTack.GetHtmlContentRemoteAsyncTask;
import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.model.Book;

import java.io.File;

/**
 * Created by jessicazeng on 10/27/15.
 */
public class DBHelper extends SQLiteOpenHelper implements GetHtmlContentRemoteAsyncTask.GetHtmlContentRemote {
    private static DBHelper instance = null;

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BookDB";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA = ",";

    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + BookList.BookEntry.TABLE_BOOKS +
            "(" + BookList.BookEntry.ROW_BOOK_ID + " INTEGER PRIMARY KEY,"
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

    public static DBHelper getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new DBHelper(ctx.getApplicationContext());
        }
        return instance;
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

    private static final String[] COLUMNS = { BookList.BookEntry.ROW_BOOK_ID, BookList.BookEntry.ROW_BOOK_NAME,
            BookList.BookEntry.ROW_BOOK_URL, BookList.BookEntry.ROW_AUTHOR_NAME, BookList.BookEntry.ROW_BOOK_IMAGE_URL,
            BookList.BookEntry.ROW_BOOK_PATH, BookList.BookEntry.ROW_BOOK_VISIT_COUNT };

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

    public Book getBook(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Book book = new Book();

        Cursor cursor = db.query(
                BookList.BookEntry.TABLE_BOOKS,           // The table to query
                COLUMNS,                                  // The columns to return
                BookList.BookEntry.ROW_BOOK_ID + " =? ",       // The columns for the WHERE clause
                new String[] { String.valueOf(bookId) },      // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (cursor != null) {
            cursor.moveToFirst();

            book.setId(cursor.getInt(0));
            book.setBookName(cursor.getString(1));
            book.setBookURL(cursor.getString(2));
            book.setAuthorName(cursor.getString(3));
            book.setBookImageURL(cursor.getString(4));
            book.setBookVisitCount(cursor.getInt(6));

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

        Cursor cursor = db.rawQuery("SELECT " + BookList.BookEntry.ROW_BOOK_VISIT_COUNT + " FROM " + BookList.BookEntry.TABLE_BOOKS +
                " WHERE " + BookList.BookEntry.ROW_BOOK_ID + " =?", new String[]{String.valueOf(book.getId())});

        if(cursor != null) {
            cursor.moveToFirst();

            ContentValues values = new ContentValues();
            values.put(BookList.BookEntry.ROW_BOOK_VISIT_COUNT, cursor.getInt(0) + 1);

            db.update(BookList.BookEntry.TABLE_BOOKS, values, BookList.BookEntry.ROW_BOOK_ID + " = ? ",
                    new String[] {String.valueOf(book.getId())});

            cursor.close();
        }

        db.close();
    }

    @Override
    public void onContentReceived(File file, Book book) {
        if(file == null) {
            Toast.makeText(context, R.string.error_get_book, Toast.LENGTH_LONG).show();
        } else {
            insertPath(file, book);
        }
    }
}
