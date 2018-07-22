package com.bbcafe.community;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String USER_ACCOUNT = "USER_ACCOUNT";
    private static final int PERMISSION_REQUEST_CODE = 200;
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
    public static void start(Activity context, GoogleSignInAccount account, String aboutData, String contactData, String songsData) {
        context.startActivityForResult(new Intent(context, MainActivity.class)
                .putExtra(USER_ACCOUNT, account).putExtra("ABOUT_DATA", aboutData).putExtra("CONTACT_DATA", contactData).putExtra("SONGS_DATA", songsData), LoginActivity.LOG_OUT);
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
    private boolean checkPermission() {
        int playFile = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int downloadFile = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return playFile == PackageManager.PERMISSION_GRANTED && downloadFile == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
   /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        //Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                        MyApplication.getInstance().trackEvent("Menu", "Click", "Songs");
                        //createSongsListFragment();
                        Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                       // createRecyclerListFragment();//News List
                        Toast.makeText(context, "Not Accepted", Toast.LENGTH_SHORT).show();
                        MyApplication.getInstance().trackEvent("Menu", "Click", "News List");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }*/
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            createRecyclerListFragment();//News List
            MyApplication.getInstance().trackEvent("Menu", "Click", "News List");
        } else if (id == R.id.nav_list2) {
            createRecyclerListFragment2();//Contact List
            MyApplication.getInstance().trackEvent("Menu", "Click", "Contact List");
        } else if (id == R.id.nav_about_us) {
            //showAboutUs("http://www.courtalam.com/");
            MyApplication.getInstance().trackEvent("Menu", "Click", "About us");
            Intent intent = new Intent(MainActivity.this, HtmlContent.class);
            intent.putExtra("ABOUT_DATA", getIntent().getStringExtra("ABOUT_DATA")).putExtra("TITLE", "எங்களை பற்றி");
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            MyApplication.getInstance().trackEvent("Menu", "Click", "Logout");
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.nav_manage) {
            //createTextFragment("nav_manage");
            MyApplication.getInstance().trackEvent("Menu", "Click", "Songs");
            if (!checkPermission()) {
                requestPermission();
                //createSongsListFragment();
            } else {
                createSongsListFragment();
            }
        } else if (id == R.id.nav_share) {
            //createTextFragment("nav_share");
            MyApplication.getInstance().trackEvent("Menu", "Click", "App Share");
            shareIt();
        } else if (id == R.id.nav_send) {
            MyApplication.getInstance().trackEvent("Menu", "Click", "To Contact");
            Intent intent = new Intent(MainActivity.this, HtmlContent.class);
            intent.putExtra("ABOUT_DATA", getIntent().getStringExtra("CONTACT_DATA")).putExtra("TITLE", "தொடர்புக்கு");
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

    void createSongsListFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, SongsListFragment.newInstance(getIntent().getStringExtra("SONGS_DATA")), SongsListFragment.TAG);
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
