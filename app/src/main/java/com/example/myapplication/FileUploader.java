package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploader extends AsyncTask<File, Void, Void> {

    private static final String TAG = "FileUploader";
    private String SERVER_URL = "http://192.168.2.89:8080/postback"; // Change this to your server URL
    private String KEY = "";
    private ProgressBar progressBar;

    public FileUploader(ProgressBar progressBar, String server_url, String key) {
        this.progressBar = progressBar;
        this.SERVER_URL = server_url;
        this.KEY = key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected Void doInBackground(File... files) {
        if (files.length == 0)
            return null;

        File file = files[0];
        if (validateKey()) {
            uploadFile(file);
        } else {
            Log.e(TAG, "Invalid key.");
        }

        return null;
    }

    private boolean validateKey() {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("key", KEY);
            connection.setRequestProperty("validate", "true");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Write a dummy request to trigger key validation
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("");
            writer.flush();
            writer.close();

            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                Log.e(TAG, "Key validation failed: " + responseCode);
                return false;
            } else {
                return true; // Key is valid
            }
        } catch (IOException e) {
            Log.e(TAG, "Error validating key: " + e.getMessage());
            return false;
        }
    }

    private void uploadFile(File file) {
        String[] filenameparts = file.toString().split("/");
        String filename = filenameparts[filenameparts.length - 1];

        try {
            // Open a connection to the server
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", filename);
            connection.setRequestProperty("key", KEY);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Create a data output stream to write the file data
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            // Write the file data to the output stream
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            // Close the output stream
            outputStream.flush();
            outputStream.close();

            // Get the response from the server (optional)
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            Log.d(TAG, "Response Code: " + responseCode);
            Log.d(TAG, "Response Message: " + responseMessage);

            // Disconnect the connection
            connection.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "Error uploading file: " + e.getMessage());
        }
    }
}
