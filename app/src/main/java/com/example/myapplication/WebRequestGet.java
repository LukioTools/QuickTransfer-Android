package com.example.myapplication;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebRequestGet extends AsyncTask<String, Void, String> {

    // This method will be executed on a background thread
    @Override
    protected String doInBackground(String... urls) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Read the response
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close connections
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    // This method will be executed on the UI thread after doInBackground() finishes
    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
        super.onPostExecute(result);
        // Handle the result here, update UI, etc.
    }
}