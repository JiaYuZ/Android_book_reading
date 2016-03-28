package com.example.jessicaz.readbook.fragments.searchresult;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jessicaz.readbook.AsyncTack.GetSearchResultAsyncTask;
import com.example.jessicaz.readbook.Interface.SwitchFragment;
import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.helper.DBHelper;
import com.example.jessicaz.readbook.model.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jessicazeng on 3/18/16.
 */
public class SearchResultFragment extends Fragment implements SearchResultAdapter.Listener, GetSearchResultAsyncTask.GetSearchResult {
    @Bind(R.id.search_result_recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.loading_spinner)
    RelativeLayout spinner;
    @Bind(R.id.state_message)
    TextView stateMessage;

    SearchResultAdapter adapter;
    List<Book> booksList = new ArrayList<>();
    GetSearchResultAsyncTask getSearchResultAsyncTask;
    String query;

    private static final String TYPE_REMOTE = "REMOTE BOOKS";
    private static final String TYPE_LOCAL = "LOCAL BOOKS";
    private static Handler sHandler = new Handler();
    private long startLoadTimestamp;

    private DBHelper dbHelper;

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
        startLoadTimestamp = new Date().getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_result_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);

        adapter = new SearchResultAdapter(getActivity(), new ArrayList<SearchResultAdapter.Item>(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        Runnable spinnerDisplay = new Runnable() {
            @Override
            public void run() {
                AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
                fadeOutAnimation.setStartOffset(1000);
                fadeOutAnimation.setDuration(500);
                fadeOutAnimation.setFillAfter(true);

                spinner.startAnimation(fadeOutAnimation);
            }
        };

        long nowTimestamp = new Date().getTime();
        long diff = nowTimestamp - startLoadTimestamp;

        if(diff > 1000) {
            spinnerDisplay.run();
        } else {
            sHandler.postDelayed(spinnerDisplay, 1000 - diff);
        }

        getSearchResultAsyncTask = new GetSearchResultAsyncTask(this, query);
        getSearchResultAsyncTask.execute();
    }

    public void openBook(Book book) {
        String bookPath;

        if(dbHelper.hasPath(book)) {
            bookPath = dbHelper.getPath(book);
        } else {
            dbHelper.createPath(book);
            bookPath = book.getBookURL();
        }

        SwitchFragment switchFragment = (SwitchFragment) getActivity();
        switchFragment.switchToBookContentFragment(bookPath);
    }

    @Override
    public void onBookClick(Book book) {
        openBook(book);
    }

    public void getSearchResultBooksList(List<Book> booksList) {
        this.booksList = booksList;

        List<SearchResultAdapter.Item> items = new ArrayList<>();
        SearchResultAdapter.Item localResult = new SearchResultAdapter.HeaderItem(TYPE_LOCAL);
        SearchResultAdapter.Item remoteResult = new SearchResultAdapter.HeaderItem(TYPE_REMOTE);

        Collections.sort(this.booksList);

        items.add(localResult);
        items.addAll(filter(this.booksList, TYPE_LOCAL));
        items.add(remoteResult);
        items.addAll(filter(this.booksList, TYPE_REMOTE));

        adapter.setItemList(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResultReceived(List<Book> booksList) {
        if(booksList == null || booksList.isEmpty()) {
            stateMessage.setVisibility(View.VISIBLE);
            stateMessage.setText(R.string.error_no_search_result);
        } else {
            getSearchResultBooksList(booksList);
        }
    }

    private List<SearchResultAdapter.Item> filter(List<Book> booksList, String type) {
        List<SearchResultAdapter.Item> localItems = new ArrayList<>();
        List<SearchResultAdapter.Item> remoteItems = new ArrayList<>();

        if (dbHelper.checkDatabase()) {
            dbHelper.openDatabase();

            if (dbHelper.checkTable()) {
                if (dbHelper.checkData()) {
                    int size = booksList.size();

                    for (int i = 0; i < size; i++) {
                        Book book = dbHelper.getBook(booksList.get(i).getId());
                        SearchResultAdapter.Item bookItem = new SearchResultAdapter.BookItem(book);

                        if(book.getBookVisitCount() > 0) {
                            localItems.add(bookItem);
                        } else {
                            remoteItems.add(bookItem);
                        }
                    }
                }
            }
        }

        if(type.equals(TYPE_LOCAL)) {
            return localItems;
        } else {
            return remoteItems;
        }
    }
}
