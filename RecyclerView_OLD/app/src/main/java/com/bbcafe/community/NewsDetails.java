package com.bbcafe.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsDetails extends AppCompatActivity {
    private TextView newsdata,newstitle;
    private ImageView newsimage;
    private Context context;
    private AdView mAdView;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final String jsonArguments = getIntent().getStringExtra("DETAILS");

        try {
            jsonObject = new JSONObject(jsonArguments);
            newsimage = findViewById(R.id.newsimage);
            newstitle = findViewById(R.id.newstitle);
            newstitle.setText(jsonObject.getString("title"));
            setTitle(jsonObject.getString("title"));
            Picasso.with(context).load(jsonObject.getString("image")).into(newsimage);
            newsdata = findViewById(R.id.newsdata);
            String strData = jsonObject.getString("content");
            //newsdata.setText(jsonObject.getString("title"));
            newsdata.setText(Html.fromHtml(strData, Html.FROM_HTML_MODE_COMPACT));
        } catch (JSONException e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        Button share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                try {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Title : "+jsonObject.getString("title"));
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "*Content :* "+jsonObject.getString("title") +" , *URL :* "+jsonObject.getString("url"));
                } catch (JSONException e) {
                    MyApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
                startActivity(Intent.createChooser(sharingIntent, "பகிர்க"));
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        mAdView.loadAd(adRequest);
    }

    public static void start(Context context, String jsonObject) {
        context.startActivity(new Intent(context, NewsDetails.class)
                .putExtra("DETAILS", jsonObject));
    }
}
