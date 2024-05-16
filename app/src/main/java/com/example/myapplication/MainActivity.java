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
    ProgressBar progressBar;
    EditText ipTextField;
    EditText keyTextField;

    String ip = "http://192.168.2.89:8080/postback";
    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(".....");
        Activity act = this;
        Intent intent = act.getIntent();

        SharedPreferences sharedPref = act.getSharedPreferences("data", MODE_PRIVATE);

        log = (TextView)findViewById(R.id.log);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ipTextField = (EditText)findViewById(R.id.ipField);
        keyTextField = (EditText)findViewById(R.id.keyField);

        progressBar.setVisibility(View.GONE);

        ip = sharedPref.getString("ip", "");
        ipTextField.setText(ip);
        key = sharedPref.getString("key", "");
        keyTextField.setText(key);

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
        keyTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                key = keyTextField.getText().toString();
                sharedPref.edit().putString("key", key).commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        if (intent != null && intent.hasExtra("android.intent.extra.STREAM")){
            Uri uri = intent.getParcelableExtra("android.intent.extra.STREAM");
            System.out.println(".....");
            log.setText(uri.getPath());
            System.out.println(uri.getPath());

            //new WebRequestPost().execute(getFileName(uri), ip);
            File f = new File(getFileName(uri));
            System.out.println("ip: " + ip);
            System.out.println("key: " + key);
            new FileUploader(progressBar, ip, key).execute(f);

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