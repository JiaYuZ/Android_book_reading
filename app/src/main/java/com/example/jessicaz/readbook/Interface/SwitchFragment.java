package com.example.jessicaz.readbook.Interface;

import com.example.jessicaz.readbook.model.Book;

/**
 * Created by jessicazeng on 10/9/15.
 */
public interface SwitchFragment {
    void switchToBookListFragment();
    void switchToBookContentFragment(String bookUrl);
    void switchToAboutFragment();
}
