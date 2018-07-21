package com.bbcafe.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bbcafe.community.network.GetResult;
import com.bbcafe.community.network.ServerRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unchecked")
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    public static final int LOG_OUT = 1001;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        // Views
        mStatusTextView = findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        findViewById(R.id.disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        if (requestCode==LOG_OUT) {
            signOut();
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            MyApplication.getInstance().trackException(e);
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            mStatusTextView.setText("Signed in as: "+ account.getDisplayName());
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            sendToServer(account);
        } else {
            mStatusTextView.setText("Signed out");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    private void sendToServer(@NonNull final GoogleSignInAccount account) {
        if (ServerRequest.isConnectedToInternet(context)) {
            final ProgressDialog progressDialog = new ProgressDialog();
            progressDialog.show(getSupportFragmentManager());
            ServerRequest.get("http://commune.bestbloggercafe.com/utilities/assign_work", new GetResult() {
                        @Override
                        public void onResult(String resultStringFromServer) {
                            progressDialog.cancel();
                            try {
                                if (!resultStringFromServer.isEmpty()) {
                                    JSONObject jsonObject = new JSONObject(resultStringFromServer);//get your result json object here
                                    Log.i(TAG,resultStringFromServer);
                                    if (jsonObject.getInt("status")==1) {
                                        String aboutData = jsonObject.getString("about");
                                        String contactData = jsonObject.getString("contact");
                                        JSONArray jsonArray = jsonObject.getJSONArray("songs");
                                        /*for (int i = 0; i<jsonArray.length(); i++) {
                                            JSONObject songsJsonObject = jsonArray.getJSONObject(i);
                                            String url = songsJsonObject.getString("url");
                                            String title = songsJsonObject.getString("title");
                                        }*/
                                        MainActivity.start(LoginActivity.this, account, aboutData, contactData, jsonArray.toString());
                                    } else {
                                        signOut();
                                        Toast.makeText(context, "Invalid User Data", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                MyApplication.getInstance().trackException(e);
                                e.printStackTrace();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Pair<>("name", account.getDisplayName()),
                    new Pair<>("photo", ""+account.getPhotoUrl()),
                    new Pair<>("email", account.getEmail()),
                    new Pair<>("mobile", "NOT_AVAILABLE"),
                    new Pair<>("appver", BuildConfig.VERSION_NAME),
                    new Pair<>("joinedon", "PARAM_VALUE"),
                    new Pair<>("lastactivity", "PARAM_VALUE"),
                    new Pair<>("dob", "PARAM_VALUE"),
                    new Pair<>("sex", "PARAM_VALUE"),
                    new Pair<>("aboutme", "PARAM_VALUE"),
                    new Pair<>("devicetoken", FirebaseInstanceId.getInstance().getToken()),
                    new Pair<>("googleid", account.getId()),
                    new Pair<>("blood", "PARAM_VALUE"),
                    new Pair<>("addr1", "PARAM_VALUE"),
                    new Pair<>("addr2", "PARAM_VALUE"),
                    new Pair<>("city", "PARAM_VALUE"),
                    new Pair<>("state", "PARAM_VALUE"),
                    new Pair<>("pincode", "PARAM_VALUE"));
        } else {
            signOut();
        }
    }
}
