package com.example.jessicaz.readbook.fragments.bookslist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jessicaz.readbook.BuildConfig;
import com.example.jessicaz.readbook.fragments.searchresult.SearchResultFragment;
import com.example.jessicaz.readbook.model.Book;
import com.example.jessicaz.readbook.Interface.GetBooksRemote;
import com.example.jessicaz.readbook.Interface.SwitchFragment;
import com.example.jessicaz.readbook.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import com.example.jessicaz.readbook.service.BookService;


/**
 * Created by JessicaZ on 9/24/15.
 */
public class BookListFragment extends Fragment implements GetBooksRemote, SearchView.OnQueryTextListener { //Better to use ListFragment?
    @Bind(R.id.book_recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.loading_spinner) RelativeLayout mSpinner;

    private static Handler sHandler = new Handler();
    private long startLoadTimestamp;

    private List<Book> mBooksList;
    private BooksListAdapter mAdapter;
    //Interface for switching fragment
    private SwitchFragment mSwitchFragment = null;
    private GetBooksRemoteAsyncTask mGetBooksRemoteAsyncTask = null;

    private class GetBooksRemoteAsyncTask extends AsyncTask<Void, Void, List<Book>> {
        BookService service;
        GetBooksRemote bookListFragment;

        public GetBooksRemoteAsyncTask(GetBooksRemote bookListFragment){
            this.bookListFragment = bookListFragment;

            Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BOOKS_JSON_OBJECT_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            service = retrofit.create(BookService.class);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Book> doInBackground(Void... args) {
            List<Book> bookList = null;
            Call<List<Book>> call = service.getBooks();

            try {
                bookList = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), R.string.error_get_book, Toast.LENGTH_LONG).show();
            }

            return bookList;
        }

        //Update UI
        @Override
        protected void onPostExecute(List<Book> result) {
            bookListFragment.onBooksReceived(result);
        }
    }

       @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        startLoadTimestamp = new Date().getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.book_list_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);
        //Set content to this listView
        mGetBooksRemoteAsyncTask = new GetBooksRemoteAsyncTask(this);
        mGetBooksRemoteAsyncTask.execute();
    }

    public void openBook(int position) {
        String bookURL = mBooksList.get(position).getBookURL();
        mSwitchFragment = (SwitchFragment) getActivity();
        mSwitchFragment.switchToBookContentFragment(bookURL, mBooksList.get(position).getBookName());
    }

    @Override
    public void onBooksReceived(List<Book> booksList) {
        this.mBooksList = booksList;

        mAdapter = new BooksListAdapter(getActivity(),booksList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new BooksListRecycleViewClickListener(
                        getActivity(), new BooksListRecycleViewClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        openBook(position);
                    }
                })
        );

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.menu_search, menu);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Fragment fragment = new SearchResultFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

//        final List<Book> filteredBooksList = filter(mBooksList, query);
//        mAdapter.animateTo(filteredBooksList);
//        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return false;
    }

    private List<Book> filter(List<Book> booksList, String query) {
        query = query.toLowerCase();

        final List<Book> filteredBooksList = new ArrayList<>();
        for (Book book : booksList) {
            final String bookName = book.getBookName().toLowerCase();
            if (bookName.contains(query)) {
                filteredBooksList.add(book);
            }
        }
        return filteredBooksList;
    }
}
