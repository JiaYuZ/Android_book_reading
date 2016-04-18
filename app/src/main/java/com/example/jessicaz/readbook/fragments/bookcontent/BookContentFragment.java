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
import android.widget.RelativeLayout;

import com.example.jessicaz.readbook.R;

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

    private String bookURL;
    private long startLoadTimestamp;
    private static Handler sHandler = new Handler();

    public BookContentFragment(){

    }

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
        if(arg != null) {
            bookURL = arg.getString("bookURL");
        }

        startLoadTimestamp = new Date().getTime();
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
        getActivity().setTitle(bookName);
    }
}

