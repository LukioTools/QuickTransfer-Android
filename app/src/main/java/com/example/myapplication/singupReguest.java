package com.example.myapplication;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class singupReguest extends AsyncTask<String, Void, Void> {

    private String USERNAME;
    private String PASSWORD;
    private String singup_URL;
    private boolean wasAble = false;

    private Activity activity;

    public singupReguest(String username, String password, String singup_URL, Activity act){
        this.USERNAME = username;
        this.PASSWORD = password;
        this.singup_URL = singup_URL;
        this.activity = act;
    }

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
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(singup_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("username", USERNAME);
            connection.setRequestProperty("password", PASSWORD);

            int responceCode = connection.getResponseCode();

            System.out.println("responce code: " + responceCode + " message: " + connection.getResponseMessage());

            if (responceCode == 200){
                wasAble = true;
            }
        }catch (IOException e){

        }
        return null;
    }
}
