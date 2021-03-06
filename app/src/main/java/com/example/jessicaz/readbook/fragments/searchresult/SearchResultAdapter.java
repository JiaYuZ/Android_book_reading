package com.example.jessicaz.readbook.fragments.searchresult;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
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
 * Created by jessicazeng on 3/18/16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int BOOK_VIEW_TYPE = 1;
    private static final int NO_RESULT_VIEW_TYPE = 2;

    Context context;
    List<Item> itemList;
    String query;
    Listener listener;
    LayoutInflater inflater;

    public static class Item<T extends Object> {
        T object;

        public Item(T object) {
            this.object = object;
        }

        T getItem(){
            return object;
        }
    }

    public interface Listener {
        void onBookClick(Book book);
    }

    public static class NoResultItem extends Item<String> {
        public NoResultItem(String object) {
            super(object);
        }
    }

    public static class BookItem extends Item<Book> {
        public BookItem(Book object) {
            super(object);
        }
    }

    public static class HeaderItem extends Item<String> {
        public HeaderItem(String object) {
            super(object);
        }
    }

    public class NoResultViewHolder extends ViewHolder<NoResultItem> {
        @Bind(R.id.no_result_textview)
        TextView noResultText;

        public NoResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void takeItem(Item item) {
            String noResult = (String) item.getItem();

            noResultText.setText(noResult);
        }

    }

    public class BookViewHolder extends ViewHolder<BookItem> {
        @Bind(R.id.book_image)
        ImageView imageView;
        @Bind(R.id.book_name_textview)
        TextView bookName;
        @Bind(R.id.author_name_textview)
        TextView authorName;

        public BookViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void takeItem(Item item) {
            final Book book = (Book) item.getItem();

            Picasso.with(context).load(book.getBookImageURL().trim()).into(imageView);
            bookName.setText(spannableString(book.getBookName(), query));
            authorName.setText(spannableString(book.getAuthorName(), query));

            bookName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBookClick(book);
                }
            });
        }
    }

    public class HeaderViewHolder extends ViewHolder<HeaderItem>{
        @Bind(R.id.search_result_list_header)
        TextView headerText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void takeItem(Item item) {
            String header = (String) item.getItem();

            headerText.setText(header);
        }
    }

    public SearchResultAdapter(Context context, List<Item> itemList, String query, Listener listener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.itemList = itemList;
        this.query = query;
        this.listener = listener;
    }

    public abstract static class ViewHolder<T extends Item> extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        abstract void takeItem(Item item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch(viewType) {
            case 0:
                view = inflater.inflate(R.layout.search_result_row_header, parent, false);
                return new HeaderViewHolder(view);
            case 1:
                view = inflater.inflate(R.layout.search_result_row, parent, false);
                return new BookViewHolder(view);
            case 2:
                view = inflater.inflate(R.layout.search_result_row_no_result, parent, false);
                return new NoResultViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.takeItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        if(itemList != null) {
            return itemList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Item item = getItem(position);

        if(item instanceof HeaderItem) {
            return HEADER_VIEW_TYPE;
        } else if(item instanceof BookItem){
            return BOOK_VIEW_TYPE;
        } else if(item instanceof NoResultItem) {
            return NO_RESULT_VIEW_TYPE;
        }

        return super.getItemViewType(position);
    }


    public Item getItem(int position) {
        return itemList.get(position);
    }

    public List<Item> getItemList(){
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public SpannableString spannableString(String string, String query) {
        if(!string.toLowerCase().contains(query.toLowerCase())) {
            return new SpannableString(string);
        }
        Spannable spannableString = new SpannableString(string);
        int startIndex = string.toLowerCase().indexOf(query.toLowerCase());
        int endIndex = startIndex + query.length();
        spannableString.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context, R.color.Gold)), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return new SpannableString(spannableString);
    }
}
