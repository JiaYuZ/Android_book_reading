package com.example.jessicaz.readbook.fragments.bookcontent;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.example.jessicaz.readbook.R;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JessicaZ on 9/24/15.
 */
public class BookContentFragment extends Fragment {
    private String mBookURL;
    private long startLoadTimestamp;

    @Bind(R.id.book_content_webview) WebView bookContentWebView;
    @Bind(R.id.loading_spinner) RelativeLayout mSpinner;

    private static Handler sHandler = new Handler();

    public BookContentFragment(){}

    public static BookContentFragment newInstance(String bookURL) {
        BookContentFragment bookContentFragment = new BookContentFragment();
        Bundle args = new Bundle();

        args.putString("bookURL", bookURL);
        bookContentFragment.setArguments(args);

        return bookContentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if(arg != null){
            mBookURL = arg.getString("bookURL");
        }

        startLoadTimestamp = new Date().getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_content_fragment, container, false);
        //Use butterknife to inflate bookcontent_layout to this view
        ButterKnife.bind(this, view);
        //Open web page with bookURL in bookContentWebView
        bookContentWebView.getSettings().setLoadWithOverviewMode(true);
        bookContentWebView.loadUrl(mBookURL);
        bookContentWebView.requestFocus();

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

        return view;
    }
}

