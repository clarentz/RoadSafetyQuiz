package msu.olive.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import msu.olive.R;
import msu.olive.Server.RequestHandler;
import msu.olive.Server.Server;

public class LoginActivity extends AppCompatActivity {

    //Save the login data with Shared Preferences
    private static final String UPLOAD_KEY_USERNAME = "username";
    private static final String UPLOAD_KEY_PASSWORD = "password";
    private static final String SHARED_PREFRENCES_NAME = "saveduser";
    //UI references
    private EditText login_username;
    private EditText login_password;
    private Button btnSignIn;
    CheckBox checkBox;
    TextView btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bumbum();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestAccessPermission();
        }


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //if blank, login error else ignore space and begin login process
                if ((login_username.getText().toString().trim().length() > 0) && (login_password.getText().toString().trim().length() > 0)) {
                    pushData();
                } else {
                    Toast.makeText(LoginActivity.this, "Please insert your username and password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pushData() { //push the inserted username and password and get the loginURL of user
        class Push extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            RequestHandler requestHandler = new RequestHandler();

            @Override
            protected String doInBackground(Void... voids) {
                final String username = login_username.getText().toString().trim();
                final String password = login_password.getText().toString().trim();

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY_USERNAME, username);
                data.put(UPLOAD_KEY_PASSWORD, password);
                String result = requestHandler.sendPostRequest(Server.LoginURL, data);
                return result;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Signing in, please wait ...", null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                if (s.equals("0")) {
                    Toast.makeText(LoginActivity.this, "Login failed! Please insert your correct information!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("data", s);
                    startActivity(intent);
                    finish();
                }
            }
        }
        Push push = new Push();
        push.execute();
    }

    private void bumbum() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFRENCES_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        int check = sharedPreferences.getInt("check", -1); // remember user
        checkBox = (CheckBox) findViewById(R.id.login_remember_me_checkbox);
        login_username = (EditText) findViewById(R.id.login_username);
        login_password = (EditText) findViewById(R.id.login_password);
        login_username.setText(username);
        login_password.setText(password);
        if (check == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // a button with 2 states: checked and unchecked
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFRENCES_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", login_username.getText().toString());
                    editor.putString("password", login_password.getText().toString());
                    editor.putInt("check", 1);
                    editor.commit();
                    Log.i ("checking", login_username.getText().toString());
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFRENCES_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("username");
                    editor.remove("password");
                    editor.putInt("check", 0);
                    editor.commit();
                    Log.i ("checking", login_username.getText().toString());
                }
            }
        });

        btnSignIn = (Button) findViewById(R.id.login_button_sign_in);
        btnRegister = (TextView) findViewById(R.id.login_button_register);
        btnRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                return false;
            }
        });




    }


    private void requestAccessPermission() {

        //request write external permission to get full size image from camera
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
        }
        //request write internal permission to get from library

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }



}
