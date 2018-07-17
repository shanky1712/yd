package com.bbcafe.community;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String USER_ACCOUNT = "USER_ACCOUNT";

    private Context context;

    private ImageView ivUserPhoto;
    private TextView tvUserName, tvUserEmail;
    //private ShareActionProvider mShareActionProvider;

    //private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ivUserPhoto = navigationView.getHeaderView(0).findViewById(R.id.ivPhoto);
        tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);

        if (getIntent().getParcelableExtra(USER_ACCOUNT)!=null) {
            populateUserDetails((GoogleSignInAccount) getIntent().getParcelableExtra(USER_ACCOUNT));
        }
        createRecyclerListFragment();
    }

    private void populateUserDetails(GoogleSignInAccount account) {
        if (ivUserPhoto!=null && tvUserName!=null && tvUserEmail!=null) {
            if (account.getPhotoUrl()!=null) {
                Picasso.with(this).load(account.getPhotoUrl()).into(ivUserPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) ivUserPhoto.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        ivUserPhoto.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        ivUserPhoto.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                });
            } else {
                ivUserPhoto.setImageResource(R.drawable.ic_launcher_foreground);
            }
            tvUserName.setText(account.getDisplayName());
            tvUserEmail.setText(account.getEmail());
        }
    }
    public static void start(Activity context, GoogleSignInAccount account, String aboutData, String contactData) {
        context.startActivityForResult(new Intent(context, MainActivity.class)
                .putExtra(USER_ACCOUNT, account).putExtra("ABOUT_DATA", aboutData).putExtra("CONTACT_DATA", contactData), LoginActivity.LOG_OUT);
    }
    /*public static void start(Activity context, GoogleSignInAccount account, String htmlData) {
        context.startActivityForResult(new Intent(context, MainActivity.class)
        .putExtra(USER_ACCOUNT, account), LoginActivity.LOG_OUT);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Locate MenuItem with ShareActionProvider
        //MenuItem item = menu.findItem(R.id.nav_share);

        // Fetch and store ShareActionProvider
        //mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }
    // Call to update the share intent
   /* private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            createRecyclerListFragment();
        } else if (id == R.id.nav_list2) {
            createRecyclerListFragment2();
        } else if (id == R.id.nav_about_us) {
            //showAboutUs("http://www.courtalam.com/");
            //Intent intent = new Intent(MainActivity.this, HtmlContent.class);
            //startActivity(intent);
            Intent intent = new Intent(MainActivity.this, HtmlContent.class);
            intent.putExtra("ABOUT_DATA", getIntent().getStringExtra("ABOUT_DATA"));
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.nav_manage) {
            //createTextFragment("nav_manage");
            showAboutUs("http://www.courtalam.com/");
        } else if (id == R.id.nav_share) {
            //createTextFragment("nav_share");
            shareIt();
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this, HtmlContent.class);
            intent.putExtra("ABOUT_DATA", getIntent().getStringExtra("CONTACT_DATA"));
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void shareIt() {
//sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hi Friends !!");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Please share this app to everyone");
        startActivity(Intent.createChooser(sharingIntent, "பகிர்க"));
    }
    void createRecyclerListFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, RecyclerListFragment.newInstance(), RecyclerListFragment.TAG);
        transaction.commit();
    }

    void createRecyclerListFragment2() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, RecyclerListFragment2.newInstance(), RecyclerListFragment2.TAG);
        transaction.commit();
    }

    void showAboutUs(String textToDisplay) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, WebViewFragment.newInstance(textToDisplay), WebViewFragment.TAG);
        transaction.commit();
    }

    void createTextFragment(String textToDisplay) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, TextFragment.newInstance(textToDisplay), TextFragment.TAG);
        transaction.commit();
    }

}
