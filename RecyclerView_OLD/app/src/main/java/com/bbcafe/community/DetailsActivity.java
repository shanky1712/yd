package com.bbcafe.community;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.CallableStatement;

public class DetailsActivity extends AppCompatActivity {

    private TextView shopname,ownername,phone,addr1,addr2,city,pin;
    private ImageView image;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String jsonArguments = getIntent().getStringExtra("DETAILS");
        //Snackbar.make(findViewById(android.R.id.content), jsonArguments, Snackbar.LENGTH_LONG).show();

        try {
            JSONObject jsonObject = new JSONObject(jsonArguments);
            final String share_name = jsonObject.getString("name");
            String share_shopname = jsonObject.getString("shopname");
            String share_mobile = jsonObject.getString("mobile");
            String share_image = jsonObject.getString("photo");
            image = findViewById(R.id.shopimage);
            Picasso.with(context).load(jsonObject.getString("photo")).into(image);
            shopname = findViewById(R.id.shopname);
            shopname.setText(jsonObject.getString("shopname"));
            ownername = findViewById(R.id.ownername);
            ownername.setText(jsonObject.getString("name"));
            phone = findViewById(R.id.phone);
            phone.setText(jsonObject.getString("mobile"));
            addr1 = findViewById(R.id.address);
            addr1.setText(jsonObject.getString("addr1"));
            addr2 = findViewById(R.id.addr2);
            addr2.setText(jsonObject.getString("addr2"));
            city = findViewById(R.id.city);
            city.setText(jsonObject.getString("city"));
            pin = findViewById(R.id.pin);
            pin.setText(jsonObject.getString("pincode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "யாதவ சொந்தங்களே !!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_name);
                startActivity(Intent.createChooser(sharingIntent, "பகிர்க"));*/
            }
        });
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadFragmentActivity.start(DetailsActivity.this, "http://commune.bestbloggercafe.com/utilities/contract_creation");
            }
        });*/
    }
    public static void start(Context context, String jsonObject) {
        context.startActivity(new Intent(context, DetailsActivity.class)
                .putExtra("DETAILS", jsonObject));
    }

}
