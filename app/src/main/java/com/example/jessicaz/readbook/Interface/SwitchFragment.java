package com.example.jessicaz.readbook.Interface;

/**
 * Created by jessicazeng on 10/9/15.
 */
public interface SwitchFragment {
    void switchToBookListFragment();
    void switchToBookContentFragment(String bookPath, String bookName);
    void switchToSearchResultFragment(String query);
    void switchToAboutFragment();
}
