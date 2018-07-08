package com.bbcafe.community.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class ServerRequest {
    static final int CONNECTION_TIME_OUT = 15000;
    static final int READ_TIME_OUT = 10000;

    public static interface GetResult {
        void onResult(String resultStringFromServer);
    }

    public static void post(String url, GetResult getResult, Pair<String, String>... params) {
        try {

            StringBuilder postData = new StringBuilder();
            for (int i=0; i<params.length; i++) {
                postData.append(URLEncoder.encode(params[i].first, "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(params[i].second, "UTF-8"));
                if (i!=(params.length-1)) {
                    postData.append("&");
                }
            }
            new PostAsyncTask(getResult).execute(url, postData.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void get(String url, GetResult getResult, Pair<String, String>... params) {
        try {
            Log.i("URL_DATA",url);
            StringBuilder getData = new StringBuilder(url);
            getData.append("?");
            for (int i=0; i<params.length; i++) {
                getData.append(URLEncoder.encode(params[i].first, "UTF-8"));
                getData.append("=");
                getData.append(URLEncoder.encode(params[i].second, "UTF-8"));
                if (i!=(params.length-1)) {
                    getData.append("&");
                }
            }
            new GetAsyncTask(getResult).execute(getData.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void get(String url, GetResult getResult) {
        new GetAsyncTask(getResult).execute(url);
    }

    public static boolean isConnectedToInternet(Context context) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
        }
        if (!isConnected) {
            Toast.makeText(context, "Please enable Wifi or Mobile Data", Toast.LENGTH_LONG).show();
        }
        return isConnected;
    }

}