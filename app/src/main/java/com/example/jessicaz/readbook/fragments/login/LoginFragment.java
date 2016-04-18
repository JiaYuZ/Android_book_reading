package com.example.jessicaz.readbook.fragments.login;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jessicaz.readbook.BuildConfig;
import com.example.jessicaz.readbook.Interface.SwitchFragment;
import com.example.jessicaz.readbook.R;
import com.example.jessicaz.readbook.service.BookService;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jessicazeng on 11/6/15.
 */
public class LoginFragment extends Fragment {
    @Bind(R.id.username_edittext)
    EditText usernameEditText;
    @Bind(R.id.password_edittext)
    EditText passwordEditText;
    @Bind(R.id.login_button)
    Button loginButton;

    private String username;
    private String password;
    private SwitchFragment mSwitchFragment = null;

    private class LoginTask extends AsyncTask<Void, Void, Response<String>> {
        BookService service;
        String username;
        String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BOOK_SERVICE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(BookService.class);
        }

        @Override
        protected Response<String> doInBackground(Void... params) {
            Call<String> call = service.login(username, password);

            try {
                return call.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            if (result.isSuccess()) {
                saveLoginInfo(username, password);

                mSwitchFragment = (SwitchFragment) getActivity();
                mSwitchFragment.switchToBookListFragment();
            } else {
                Toast.makeText(getActivity(), "Incorrect User Name or Password !!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveLoginInfo(String username, String password) {
        getActivity();
        SharedPreferences shared = getActivity().getSharedPreferences("shared", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this,view);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                LoginTask loginTask = new LoginTask(username, password);
                loginTask.execute();
            }
        });

        return view;
    }


}
