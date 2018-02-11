package com.bbcafe.community.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


public class GetAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = GetAsyncTask.class.getSimpleName();

    ServerRequest.GetResult getResult;

    GetAsyncTask(ServerRequest.GetResult getResult) {
        this.getResult = getResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected final String doInBackground(String... params) {
        HttpURLConnection conn;
        StringBuilder response = new StringBuilder();
        String getData  = params[0];
        try {
            URL obj = new URL(getData);

            conn = (HttpURLConnection) obj.openConnection();
            conn.setReadTimeout(ServerRequest.READ_TIME_OUT);
            conn.setConnectTimeout(ServerRequest.CONNECTION_TIME_OUT);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            bufferedReader.close();
            conn.disconnect();

            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        //do what ever you want with the response
        Log.d(TAG, result);
        this.getResult.onResult(result);
    }

}


