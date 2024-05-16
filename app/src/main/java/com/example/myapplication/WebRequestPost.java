package com.example.myapplication;
import android.net.Uri;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WebRequestPost extends AsyncTask<String, Void, String> {

    // This method will be executed on a background thread
    @Override
    protected String doInBackground(String... params) {
        String fileUrl = params[0];
        String serverUrl = params[1];
        String charset = "UTF-8";

        String response = "";

        try {
            File binaryFile = new File(fileUrl);
            MultipartUtility multipart = new MultipartUtility(serverUrl, charset);

            // Add file part
            System.out.println(binaryFile);
            multipart.addFilePart("file", binaryFile);

            // Add other form data if needed
            // multipart.addFormField("key", "value");

            List<String> responseList = multipart.finish();
            for (String line : responseList) {
                response += line + "\n";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }

    // This method will be executed on the UI thread after doInBackground() finishes
    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
        super.onPostExecute(result);
        // Handle the result here, update UI, etc.
    }
}