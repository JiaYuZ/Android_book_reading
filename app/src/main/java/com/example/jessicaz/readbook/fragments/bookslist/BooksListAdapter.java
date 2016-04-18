package com.example.jessicaz.readbook.fragments.bookslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jessicazeng on 10/9/15.
 */
public class BooksListAdapter extends RecyclerView.Adapter<BooksListAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private Context context;
    private List<Book> booksList;
    private Listener listener;

    public interface Listener {
        void onBookClick(Book book);
    }

    public BooksListAdapter(Context context, List<Book> booksList, Listener listener) {
        inflater = LayoutInflater.from(context);
        this.booksList = booksList;
        this.listener = listener;
        this.context = context;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        abstract void takeBook(Book book);
    }

    @Override
    public BooksListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View bookInfoLayoutView = inflater.inflate(R.layout.book_list_row, viewGroup, false);
        return new bookViewHolder(bookInfoLayoutView);
    }

    @Override
    public void onBindViewHolder(final BooksListAdapter.ViewHolder viewHolder,final int position) {
        viewHolder.takeBook(booksList.get(position));
    }

    public class bookViewHolder extends ViewHolder{
        @Bind(R.id.book_image)
        ImageView bookImage;
        @Bind(R.id.book_name_textview)
        TextView bookName;
        @Bind(R.id.author_name_textview)
        TextView authorName;

        public bookViewHolder(View bookInfoLayoutView){
            super(bookInfoLayoutView);
            ButterKnife.bind(this, bookInfoLayoutView);
        }

        @Override
        void takeBook(final Book book) {
            Picasso.with(context).load(book.getBookImageURL().trim()).into(bookImage);
            bookName.setText(book.getBookName());
            authorName.setText(book.getAuthorName());

            bookName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBookClick(book);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(booksList != null) {
            return booksList.size();
        } else {
            return 0;
        }
    }

    public void setBooksList(List<Book> booksList) {
        this.booksList = booksList;
    }

    public List<Book> getItemList(){
        return booksList;
    }
}