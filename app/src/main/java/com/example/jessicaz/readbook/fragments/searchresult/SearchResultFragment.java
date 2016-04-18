package com.example.jessicaz.readbook.fragments.searchresult;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
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
    private static final String TYPE_NO_RESULT = "No result founded";
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
        hideSoftKeyboard();

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

        adapter = new SearchResultAdapter(getActivity(), new ArrayList<SearchResultAdapter.Item>(), query, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        getSearchResultAsyncTask = new GetSearchResultAsyncTask(this, query);
        getSearchResultAsyncTask.execute();

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
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.search_result));
    }

    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            getActivity();
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
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
        switchFragment.switchToBookContentFragment(bookPath, book);
    }

    @Override
    public void onBookClick(Book book) {
        openBook(book);
    }

    public void getSearchResultBooksList(List<Book> booksList) {
        this.booksList = booksList;

        List<SearchResultAdapter.Item> items = new ArrayList<>();
        List<SearchResultAdapter.Item> localBooks = new ArrayList<>();
        List<SearchResultAdapter.Item> remoteBooks = new ArrayList<>();
        SearchResultAdapter.Item localResult = new SearchResultAdapter.HeaderItem(TYPE_LOCAL);
        SearchResultAdapter.Item remoteResult = new SearchResultAdapter.HeaderItem(TYPE_REMOTE);
        SearchResultAdapter.Item noResult = new SearchResultAdapter.NoResultItem(TYPE_NO_RESULT);

        items.clear();
        localBooks.clear();
        remoteBooks.clear();

        Collections.sort(this.booksList);

        localBooks.addAll(filter(this.booksList, TYPE_LOCAL));
        remoteBooks.addAll(filter(this.booksList, TYPE_REMOTE));

        items.add(localResult);
        if(localBooks.isEmpty()) {
            items.add(noResult);
        } else {
            items.addAll(localBooks);
        }

        items.add(remoteResult);
        if(remoteBooks.isEmpty()) {
            items.add(noResult);
        } else {
            items.addAll(remoteBooks);
        }

        adapter.setItemList(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResultReceived(List<Book> booksList) {
        if(booksList == null || booksList.isEmpty()) {
            stateMessage.setVisibility(View.VISIBLE);
            stateMessage.setText(R.string.error_no_search_result);
        } else {
            stateMessage.setVisibility(View.INVISIBLE);
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
