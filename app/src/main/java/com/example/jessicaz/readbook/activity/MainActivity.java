package com.example.jessicaz.readbook.activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.fragments.about.AboutFragment;
import com.example.jessicaz.readbook.fragments.bookcontent.BookContentFragment;
import com.example.jessicaz.readbook.fragments.bookslist.BookListFragment;
import com.example.jessicaz.readbook.Interface.SwitchFragment;
import com.example.jessicaz.readbook.fragments.login.LoginFragment;


public class MainActivity extends AppCompatActivity implements SwitchFragment {
    private FragmentTransaction fragmentTransaction;
    //private LoginFragment loginFragment;
    private BookListFragment bookListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loginFragment = new LoginFragment();
        bookListFragment = new BookListFragment();

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_layout, bookListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch (item.getItemId()) {
            //If selected About on menu, switch to aboutFragment
            case R.id.about_menu_item:
                switchToAboutFragment();
                return true;
            //If selected Go To Gutenberg on menu, open Gutenberg web page on browser
            case R.id.go_to_gutenberg_menu_item:
                goToGutenberg();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToGutenberg() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gutenberg.org/"));
        startActivity(browserIntent);
    }

    @Override
    public void onBackPressed(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
    }

    //TODO set title in each fragment
    @Override
    public void switchToBookListFragment() {
        setTitle(R.string.app_name);
        BookListFragment bookListFragment = new BookListFragment();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, bookListFragment).commit();
    }

    @Override
    public void switchToBookContentFragment(String bookUrl) {
        //setTitle(book.getBookName());
        //Created bookContentFragment with bookURL reference
        BookContentFragment bookContentFragment = BookContentFragment.newInstance(bookUrl);
        FragmentTransaction(bookContentFragment);
    }

    @Override
    public void switchToAboutFragment() {
        setTitle("About");
        AboutFragment aboutFragment = new AboutFragment();
        FragmentTransaction(aboutFragment);
    }

    private void FragmentTransaction(Fragment fragment){
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                R.animator.slide_in_right, R.animator.slide_out_left);
        fragmentTransaction.replace(R.id.main_layout, fragment);
        fragmentTransaction.addToBackStack(null).commit();
    }
}
