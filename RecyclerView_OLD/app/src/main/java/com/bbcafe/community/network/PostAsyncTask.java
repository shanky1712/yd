package com.bbcafe.community.network;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.bbcafe.community.LoginActivity;

import org.json.JSONObject;

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


public class PostAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = PostAsyncTask.class.getSimpleName();

    ServerRequest.GetResult getResult;

    PostAsyncTask(ServerRequest.GetResult getResult) {
        this.getResult = getResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected final String doInBackground(String... params) {
        URL myUrl = null;
        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();
        String url  = params[0];
        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(ServerRequest.READ_TIME_OUT);
            conn.setConnectTimeout(ServerRequest.CONNECTION_TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //one long string, first encode is the key to get the  data on your web
            //page, second encode is the value, keep concatenating key and value.
            //theres another ways which easier then this long string in case you are
            //posting a lot of info, look it up.
            String postData = params[1];
            OutputStream os = conn.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bufferedWriter.write(postData);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            conn.disconnect();
            os.close();
        }  catch (IOException e) {
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


