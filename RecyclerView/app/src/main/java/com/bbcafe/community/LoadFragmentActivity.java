package com.bbcafe.community;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LoadFragmentActivity extends AppCompatActivity {

    private static final String URL_TO_LOAD = "http://commune.bestbloggercafe.com/utilities/view_all_resources";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });

        if (getIntent().getStringExtra(URL_TO_LOAD)!=null) {
            createWebViewFragment(getIntent().getStringExtra(URL_TO_LOAD));
        } else {
            Snackbar.make(this.findViewById(android.R.id.content), "Unable to find URL", Snackbar.LENGTH_LONG).show();
        }
    }

    public static void start(Context context, String urlToLoad) {
        context.startActivity(new Intent(context, LoadFragmentActivity.class)
        .putExtra(URL_TO_LOAD, urlToLoad));
    }

    void createWebViewFragment(String urlToLoad) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, WebViewFragment.newInstance(urlToLoad), WebViewFragment.TAG);
        transaction.commit();
    }

}
