package com.example.jessicaz.readbook.fragments.searchresult;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.model.Book;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jessicazeng on 3/18/16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int BOOK_VIEW_TYPE = 1;

    Context context;
    List<Item> itemList;
    Listener listener;
    Inflater inflater;

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

    public class BookViewHolder extends ViewHolder<BookItem> {
        public BookViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void takeItem(Item item) {

        }
    }

    public class HeaderViewHolder extends ViewHolder<HeaderItem>{
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void takeItem(Item item) {

        }
    }

    public SearchResultAdapter(Context context, List<Item> itemList, Listener listener) {
        this.context = context;
        this.itemList = itemList;
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
                view = View.inflate(context, R.layout.search_result_row_header, parent);
                return new HeaderViewHolder(view);
            case 1:
                view = View.inflate(context, R.layout.search_result_row, parent);
                return new BookViewHolder(view);
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
}
