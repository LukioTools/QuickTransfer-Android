package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploader extends AsyncTask<File, Void, Void> {

    private static final String TAG = "FileUploader";
    private String SERVER_URL = "http://192.168.2.89:8080/postback"; // Change this to your server URL

    private ProgressBar progressBar;

    public FileUploader(ProgressBar progressBar, String server_url){
        this.progressBar = progressBar;
        this.SERVER_URL = server_url;
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
        String[] filenameparts = files[0].toString().split("/");
        String filename = filenameparts[filenameparts.length - 1];
        System.out.println(files[0]);

        try {
            // Open a connection to the server
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", filename);
            //connection.setRequestProperty("key", "hello");
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
                System.out.println("Writing...");

            }
            fileInputStream.close();

            // Close the output stream
            outputStream.flush();
            outputStream.close();

            // Get the response from the server (optional)
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            System.out.println(responseCode);
            System.out.println(responseMessage);

            // Disconnect the connection
            connection.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "Error uploading file: " + e.getMessage());
        }

        return null;
    }
}
