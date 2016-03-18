package com.example.jessicaz.readbook.fragments.about;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jessicaz.readbook.R;

/**
 * Created by jessicazeng on 10/15/15.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        return inflater.inflate(R.layout.about_fragment,container,false);
    }
}
