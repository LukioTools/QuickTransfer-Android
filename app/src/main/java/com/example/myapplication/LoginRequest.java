package com.example.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginRequest extends AsyncTask<String, Void, Void> {

    private String USERNAME;
    private String PASSWORD;
    private String Login_URL;
    private boolean wasAble = false;

    private Activity activity;


    public LoginRequest(String username, String password, String LoginUsr_URL, Activity act){
        this.USERNAME = username;
        this.PASSWORD = password;
        this.Login_URL = LoginUsr_URL;
        this.activity = act;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (wasAble){
            activity.setContentView(R.layout.activity_main);
            if (activity instanceof MainActivity){
                ((MainActivity) activity).SendScreen(activity);
            }
        }
    }

    @Override
    protected Void doInBackground(String... string) {
        try {
            System.out.println("Login url: " + Login_URL);
            URL url = new URL(Login_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("username", USERNAME);
            connection.setRequestProperty("password", PASSWORD);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("");
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();

            System.out.println("responce: " + responseCode + " message: " + connection.getResponseMessage());

            if (responseCode == 200){
                wasAble = true;
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
