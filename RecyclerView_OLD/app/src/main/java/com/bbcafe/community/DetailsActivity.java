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

    private TextView shopname,ownername,phone,addr1,addr2,city,pin,landline;
    private ImageView image;
    private Context context;
    JSONObject jsonObject;
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
            jsonObject = new JSONObject(jsonArguments);
            image = findViewById(R.id.shopimage);
            Picasso.with(context).load(jsonObject.getString("photo")).into(image);
            shopname = findViewById(R.id.shopname);
            shopname.setText(jsonObject.getString("shopname"));
            setTitle(jsonObject.getString("shopname"));
            ownername = findViewById(R.id.ownername);
            ownername.setText(jsonObject.getString("name"));
            phone = findViewById(R.id.phone);
            phone.setText(jsonObject.getString("mobile"));
            landline = findViewById(R.id.landLine);
            landline.setText(jsonObject.getString("landline"));
            addr1 = findViewById(R.id.address);
            addr1.setText(jsonObject.getString("addr1"));
            addr2 = findViewById(R.id.addr2);
            addr2.setText(jsonObject.getString("addr2"));
            city = findViewById(R.id.city);
            city.setText(jsonObject.getString("city"));
            pin = findViewById(R.id.pin);
            pin.setText(jsonObject.getString("pincode"));
        } catch (JSONException e) {
            MyApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        Button share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*final String jsonArguments = getIntent().getStringExtra("DETAILS");
                JSONObject jsonObject1 = null;
                try {
                    jsonObject1 = new JSONObject(jsonArguments);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                MyApplication.getInstance().trackEvent("Contact", "Share", "Business Contacts shared");
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                try {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shop Name : "+jsonObject.getString("shopname"));
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "*Shop Name :* "+jsonObject.getString("shopname") +" , *Owner :* "+jsonObject.getString("name")+" , *Mobile:*"+jsonObject.getString("mobile")+ " , *Address:* "+jsonObject.getString("addr1")+","+jsonObject.getString("addr2")+ ","+jsonObject.getString("city")+","+jsonObject.getString("pincode"));
                } catch (JSONException e) {
                    MyApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
                startActivity(Intent.createChooser(sharingIntent, "பகிர்க"));
            }
        });
    }
    public static void start(Context context, String jsonObject) {
        context.startActivity(new Intent(context, DetailsActivity.class)
                .putExtra("DETAILS", jsonObject));
    }

}
