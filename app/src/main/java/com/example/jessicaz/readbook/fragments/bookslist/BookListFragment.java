package com.example.jessicaz.readbook.fragments.bookslist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jessicaz.readbook.AsyncTack.GetBooksRemoteAsyncTack;
import com.example.jessicaz.readbook.Interface.SwitchFragment;
import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.fragments.searchresult.SearchResultFragment;
import com.example.jessicaz.readbook.helper.DBHelper;
import com.example.jessicaz.readbook.model.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by JessicaZ on 9/24/15.
 */
public class BookListFragment extends Fragment implements GetBooksRemoteAsyncTack.GetBooksRemote, SearchView.OnQueryTextListener, BooksListAdapter.Listener { //Better to use ListFragment?
    @Bind(R.id.book_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.loading_spinner)
    RelativeLayout mSpinner;
    @Bind(R.id.state_message)
    TextView stateMessage;

    private static Handler sHandler = new Handler();
    private long startLoadTimestamp;
    private DBHelper dbHelper;

    private List<Book> booksList = new ArrayList<>();
    private BooksListAdapter adapter;

    private SwitchFragment switchFragment = null;
    private GetBooksRemoteAsyncTack getBooksRemoteAsyncTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        startLoadTimestamp = new Date().getTime();
        dbHelper = new DBHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_list_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);

        adapter = new BooksListAdapter(getActivity(), new ArrayList<Book>(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);

        Runnable spinnerDisplay = new Runnable() {
            @Override
            public void run() {
                AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
                fadeOutAnimation.setStartOffset(1000);
                fadeOutAnimation.setDuration(500);
                fadeOutAnimation.setFillAfter(true);

                mSpinner.startAnimation(fadeOutAnimation);
            }
        };

        long nowTimestamp = new Date().getTime();
        long diff = nowTimestamp - startLoadTimestamp;

        if(diff > 1000) {
            spinnerDisplay.run();
        } else {
            sHandler.postDelayed(spinnerDisplay, 1000 - diff);
        }

        getBooksList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.menu_search, menu);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onBooksReceived(List<Book> booksList) {
        this.booksList = booksList;

        if(booksList == null || booksList.isEmpty()) {
            stateMessage.setText(R.string.error_get_book);
            stateMessage.setVisibility(View.VISIBLE);
        } else {
            for(int i = 0; i < booksList.size(); i++) {
                dbHelper.addBook(booksList.get(i));
            }
            adapter.setBooksList(booksList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBookClick(Book book) {
        dbHelper.increaseVisit(book);
        openBook(book);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Fragment fragment = new SearchResultFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return false;
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

    public void getBooksList() {
        if(dbHelper.checkDatabase()) {
            dbHelper.openDatabase();

            if(dbHelper.checkTable()) {
                if(dbHelper.checkData()) {
                    booksList.clear();
                    for(int i = 1; i <= dbHelper.getCount(); i++) {
                        booksList.add(dbHelper.getBook(i));
                    }

                    adapter.setBooksList(booksList);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }

        getBooksRemoteAsyncTask = new GetBooksRemoteAsyncTack(this);
        getBooksRemoteAsyncTask.execute();
    }
}
