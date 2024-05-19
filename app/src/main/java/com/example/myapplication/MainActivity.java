package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView log;

    String ip = "http://192.168.2.89:8080/";

    String username = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Activity act = this;
        Intent intent = act.getIntent();

        SharedPreferences sharedPref = act.getSharedPreferences("data", MODE_PRIVATE);

        username = sharedPref.getString("username", "");

        if(username == "") {
            setContentView(R.layout.login_layouy);
            LoginScreen(act);
        }else {
            password = sharedPref.getString("password", "");
            SendScreen(act);
        }

    }

    void LoginScreen(Activity act){
        SharedPreferences sharedPref = act.getSharedPreferences("data", MODE_PRIVATE);

        EditText username_field = (EditText)findViewById(R.id.username);
        EditText password_field = (EditText)findViewById(R.id.password);
        EditText ip_field = (EditText)findViewById(R.id.ipField_login);

        Button login = (Button)findViewById(R.id.login_btn);
        Button singup = (Button)findViewById(R.id.singup_btn);

        ip = sharedPref.getString("ip", "");
        ip_field.setText(ip);

        username_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username = username_field.getText().toString();
                sharedPref.edit().putString("username", username).commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = password_field.getText().toString();
                sharedPref.edit().putString("password", password).commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ip_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ip = ip_field.getText().toString();
                sharedPref.edit().putString("ip",ip).commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username != "" && password != "") {
                    System.out.println("loginin...");
                    new LoginRequest(username, password, ip + "verifyUser", act).execute("");
                }
            }
        });

        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username != "" && password != "") {
                    System.out.println("singing up...");
                    new singupReguest(username, password, ip + "singup", act).execute("");
                }
            }
        });
    }

    public void SendScreen(Activity act){
        Intent intent = act.getIntent();
        SharedPreferences sharedPref = act.getSharedPreferences("data", MODE_PRIVATE);

        log = (TextView)findViewById(R.id.log);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        EditText ipTextField = (EditText)findViewById(R.id.ipField_Main);
        Button logoutButton = (Button)findViewById(R.id.logout);


        progressBar.setVisibility(View.GONE);

        ip = sharedPref.getString("ip", "");
        ipTextField.setText(ip);

        username = sharedPref.getString("username", "");
        password = sharedPref.getString("password", "");

        ipTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ip = ipTextField.getText().toString();
                sharedPref.edit().putString("ip",ip).commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.edit().putString("username", "").commit();
                sharedPref.edit().putString("password", "").commit();
                setContentView(R.layout.login_layouy);
                LoginScreen(act);
            }
        });

        if (intent != null && intent.hasExtra("android.intent.extra.STREAM")){
            Uri uri = intent.getParcelableExtra("android.intent.extra.STREAM");
            log.setText(uri.getPath());

            //new WebRequestPost().execute(getFileName(uri), ip);
            File f = new File(getFileName(uri));
            new FileUploader(act, progressBar, ip, "postback", "singup", "verifyUser", username, password).execute(f);

        }
    }

    String getFileName(Uri uri){
        String name = "";
        android.content.ContentResolver content = getContentResolver();
        Cursor c = content.query(uri, null, null, null, null);
        c.moveToFirst();
        int colIdx = c.getColumnIndex("_data");
        name = c.getString(colIdx);
        c.close();

        return name;
    }
}