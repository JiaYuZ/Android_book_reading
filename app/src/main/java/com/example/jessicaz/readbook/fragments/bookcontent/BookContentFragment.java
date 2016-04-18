package com.example.jessicaz.readbook.fragments.bookcontent;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.helper.DBHelper;
import com.example.jessicaz.readbook.model.Book;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JessicaZ on 9/24/15.
 */
public class BookContentFragment extends Fragment {
    @Bind(R.id.book_content_webview)
    WebView webView;
    @Bind(R.id.loading_spinner)
    RelativeLayout spinner;

    private static final String BUNDLE_KEY_BOOK_PATH = "bookPath";
    private static final String BUNDLE_KEY_BOOK_NAME = "bookName";
    private static final String BUNDLE_KEY_BOOK_ID = "bookId";

    private static Handler sHandler = new Handler();
    private long startLoadTimestamp;

    private String bookURL;
    private String bookName;
    private int bookId;
    private DBHelper dbHelper;
    private int scrollY = 0;

    public BookContentFragment(){

    }

    public static BookContentFragment newInstance(String bookPath, Book book) {
        BookContentFragment bookContentFragment = new BookContentFragment();
        Bundle args = new Bundle();

        args.putString(BUNDLE_KEY_BOOK_PATH, bookPath);
        args.putString(BUNDLE_KEY_BOOK_NAME, book.getBookName());
        args.putInt(BUNDLE_KEY_BOOK_ID, book.getId());
        bookContentFragment.setArguments(args);

        return bookContentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if(arg != null) {
            bookURL = arg.getString(BUNDLE_KEY_BOOK_PATH);
            bookName = arg.getString(BUNDLE_KEY_BOOK_NAME);
            bookId = arg.getInt(BUNDLE_KEY_BOOK_ID);
        }

        dbHelper = new DBHelper(getActivity());
        startLoadTimestamp = new Date().getTime();
        dbHelper.openDatabase();
        scrollY = dbHelper.getScrollY(bookId);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_content_fragment, container, false);
   }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
               super.onPageFinished(view, url);
               webView.scrollTo(0, scrollY);
            }
        });
        webView.loadUrl(bookURL);
        webView.requestFocus();

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
    public void onPause() {
        super.onPause();
        dbHelper.updateScrollY(bookId, webView.getScrollY());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(bookName);
    }
}

