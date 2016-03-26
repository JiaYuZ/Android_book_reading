package com.example.jessicaz.readbook.fragments.searchresult;

import android.app.Fragment;
import android.content.ClipData;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.jessicaz.readbook.AsyncTack.GetBooksRemoteAsyncTack;
import com.example.jessicaz.readbook.AsyncTack.GetSearchResultAsyncTask;
import com.example.jessicaz.readbook.Interface.SwitchFragment;
import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.helper.DBHelper;
import com.example.jessicaz.readbook.model.Book;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jessicazeng on 3/18/16.
 */
public class SearchResultFragment extends Fragment implements SearchResultAdapter.Listener, GetSearchResultAsyncTask.GetSearchResult {
    @Bind(R.id.search_result_recyclerview)
    RecyclerView recyclerView;

    SearchResultAdapter adapter;
    List<Book> booksList = new ArrayList<>();
    List<String> headerList = new ArrayList<>();
    GetSearchResultAsyncTask getSearchResultAsyncTask;
    String query;

    private DBHelper dbHelper;
    private SwitchFragment switchFragment = null;

    public SearchResultFragment(){

    }

    public static SearchResultFragment newInstance(String query) {
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle args = new Bundle();

        args.putString("Query", query);
        searchResultFragment.setArguments(args);

        return searchResultFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If has OptionMenu don't set
        setHasOptionsMenu(true);

        Bundle arg = getArguments();
        if(arg != null) {
            query = arg.getString("Query");
        }

        dbHelper = new DBHelper(getActivity());
        getSearchResultAsyncTask = new GetSearchResultAsyncTask(this, query);
        getSearchResultAsyncTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_result_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);

        adapter = new SearchResultAdapter(getActivity(), new ArrayList<SearchResultAdapter.Item>(), this);
    }


    public void openBook(Book book) {
        String bookPath;

        if(dbHelper.hasPath(book)) {
            bookPath = dbHelper.getPath(book);
        } else {
            dbHelper.createPath(book);
            bookPath = book.getBookURL();
        }

        switchFragment = (SwitchFragment) getActivity();
        switchFragment.switchToBookContentFragment(bookPath);
    }

    @Override
    public void onBookClick(Book book) {
        openBook(book);
    }

    public void getSearchResultBooksList(List<Book> booksList) {
       List<SearchResultAdapter.Item> items = new ArrayList<>();

        if (dbHelper.checkDatabase()) {
            dbHelper.openDatabase();

            if (dbHelper.checkTable()) {
                if (dbHelper.checkData()) {
                    int size = dbHelper.getCount();

                    for (int i = 1; i <= size; i++) {
                        SearchResultAdapter.Item book = new SearchResultAdapter.BookItem(dbHelper.getBook(i));
                        items.add(book);
                    }
                    Collections.sort(this.booksList);
                    adapter.setItemList(items);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onResultReceived(List<Book> bookList) {
        this.booksList = bookList;

        getSearchResultBooksList(bookList);
    }
}
