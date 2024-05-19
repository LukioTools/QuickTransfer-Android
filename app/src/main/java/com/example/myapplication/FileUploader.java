package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploader extends AsyncTask<File, Void, Void> {

    private static final String TAG = "FileUploader";
    private String SERVER_URL_POSTBACK = ""; // Change this to your server URL
    private String SERVER_URL_SINGUP = ""; // Change this to your server URL
    private String SERVER_URL_VERIFY = ""; // Change this to your server URL
    private String USERNAME = "";
    private String PASSWORD = "";
    private ProgressBar progressBar;

    public FileUploader(ProgressBar progressBar, String server_URL, String Postback_URL, String SingUp_URL, String UsrAuth, String username, String passowrd) {
        this.progressBar = progressBar;
        this.SERVER_URL_POSTBACK = server_URL + Postback_URL;
        this.SERVER_URL_SINGUP = server_URL + SingUp_URL;
        this.SERVER_URL_VERIFY = server_URL + UsrAuth;
        this.USERNAME = username;
        this.PASSWORD = passowrd;
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

        //createAccount();


        if (validateUser()) {
            uploadFile(file);
        } else {
            Log.e(TAG, "Invalid key.");
        }

        return null;
    }

    private void createAccount(String username, String password){
        try{
            URL url = new URL(SERVER_URL_SINGUP);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("password", password);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            BufferedReader br;
            if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String strCurrentLine;
            while ((strCurrentLine = br.readLine()) != null) {
                System.out.println(strCurrentLine);
            }

        }catch (IOException e){

        }
    }

    private boolean validateUser() {
        try {
            URL url = new URL(SERVER_URL_POSTBACK);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("username", USERNAME);
            connection.setRequestProperty("password", PASSWORD);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Write a dummy request to trigger key validation
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("");
            writer.flush();
            writer.close();

            // Get response
            int responseCode = connection.getResponseCode();

            if(responseCode == 200) {
                return true; // Key is valid
            }
            return false;

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
            URL url = new URL(SERVER_URL_POSTBACK);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("username", USERNAME);
            connection.setRequestProperty("password", PASSWORD);
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
