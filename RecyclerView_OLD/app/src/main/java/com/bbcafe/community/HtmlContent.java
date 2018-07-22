package com.bbcafe.community;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbcafe.community.network.ServerRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
public class HtmlContent extends AppCompatActivity {
    private TextView shopname;
    private Context context;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_content);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //final String jsonArguments = getIntent().getStringExtra("first_name");
        //sendToServer();
        String htmlAboutContent = getIntent().getStringExtra("ABOUT_DATA");
        setTitle(getIntent().getStringExtra("TITLE"));
        shopname = findViewById(R.id.content);
        shopname.setText(Html.fromHtml(htmlAboutContent, Html.FROM_HTML_MODE_COMPACT));

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

    /*private void sendToServer() {

            final ProgressDialog progressDialog = new ProgressDialog();
            progressDialog.show(getSupportFragmentManager());
            ServerRequest.get("http://192.168.1.103/commune/utilities/contactAboutus", new GetResult() {
                        @Override
                        public void onResult(String resultStringFromServer) {
                            progressDialog.cancel();
                            try {
                                if (!resultStringFromServer.isEmpty()) {
                                    JSONObject jsonObject = new JSONObject(resultStringFromServer);//get your result json object here
                                    Log.d("datchk" , String.valueOf(jsonObject.getInt("status")));
                                } else {
                                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }*/
}
