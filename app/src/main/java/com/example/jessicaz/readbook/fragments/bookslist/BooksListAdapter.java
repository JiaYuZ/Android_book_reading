package com.example.jessicaz.readbook.fragments.bookslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.model.Book;
import com.loopj.android.image.SmartImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jessicazeng on 10/9/15.
 */
public class BooksListAdapter extends RecyclerView.Adapter<BooksListAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<Book> mBooksList;

    public BooksListAdapter(Context context, List<Book> booksList) {
        mInflater = LayoutInflater.from(context);
        mBooksList = booksList;
    }

    @Override
    public BooksListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View bookInfoLayoutView = mInflater.inflate(R.layout.book_list_row, viewGroup, false);
        return new ViewHolder(bookInfoLayoutView);
    }

    @Override
    public void onBindViewHolder(final BooksListAdapter.ViewHolder viewHolder,final int position) {
        viewHolder.bookImage.setImageUrl(mBooksList.get(position).getBookImageURL());
        viewHolder.bookName.setText(mBooksList.get(position).getBookName());
        viewHolder.authorName.setText(mBooksList.get(position).getAuthorName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.book_image) SmartImageView bookImage;
        @Bind(R.id.book_name_textview) TextView bookName;
        @Bind(R.id.author_name_textview) TextView authorName;
        //@Bind(R.id.ratingbar) RatingBar rating;

        public ViewHolder(View bookInfoLayoutView){
            super(bookInfoLayoutView);
            ButterKnife.bind(this, bookInfoLayoutView);
        }
    }

    @Override
    public int getItemCount() {
        if(mBooksList != null) {
            return mBooksList.size();
        } else {
            return 0;
        }
    }

    public void animateTo(List<Book> booksList){
        applyAndAnimateRemovals(booksList);
        applyAndAnimateAdditions(booksList);
        applyAndAnimateMovedBooks(booksList);
    }

    private void applyAndAnimateRemovals(List<Book> newBooksList) {
        for (int i = mBooksList.size() - 1; i >= 0; i--) {
            final Book book = mBooksList.get(i);
            if(!newBooksList.contains(book)){
                removeBooks(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Book> newBooksList) {
        for (int i = 0, count = newBooksList.size(); i <count; i++) {
            final Book book = newBooksList.get(i);
            if(!mBooksList.contains(book)){
                addBooks(i, book);
            }
        }
    }

    private void applyAndAnimateMovedBooks(List<Book> newBooksList) {
        for (int toPosition = newBooksList.size() - 1; toPosition >= 0; toPosition --) {
            final Book books = newBooksList.get(toPosition);
            final int fromPosition = mBooksList.indexOf(books);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveBooks(fromPosition, toPosition);
            }
        }
    }

    public Book removeBooks(int position){
        final Book books = mBooksList.remove(position);
        notifyItemRemoved(position);
        return books;
    }

    public void addBooks(int position, Book books){
        mBooksList.add(position, books);
        notifyItemInserted(position);
    }

    public void moveBooks(int fromPosition, int toPosition){
        final Book book = mBooksList.remove(fromPosition);
        mBooksList.add(toPosition, book);
        notifyItemMoved(fromPosition, toPosition);
    }
}