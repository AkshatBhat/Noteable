package com.akshat.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private NavigationView navigation;
    private DrawerLayout drawer;
    private TextView displayusername;
    private ImageView displayimage;
    private TextView displayemail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private static final String TAG = "GoogleSignInTAG";
    private ProgressDialog progressDialog;
    private static final String DIALOGMESSAGE = "Logging out ...";
    private View headerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigation = findViewById(R.id.nav_view);
        headerview = navigation.getHeaderView(0);
        displayusername = headerview.findViewById(R.id.display_username);
        displayemail = headerview.findViewById(R.id.display_email);
        displayimage = headerview.findViewById(R.id.profile_image);
        setSupportActionBar(toolbar);
        //drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //adds the rotating hamburger toggle animation

        //navigation view add
        navigation.setNavigationItemSelectedListener(this
//                new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                return false;
//            }
//        } //the above is do-able but for code simplicity we implement
//        this method and pass the class' reference
        );
        //this will set listener on navigation drawer

        //
        if(savedInstanceState==null) {//find out later why
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new HomeFragment())
                    .commit();
            navigation.setCheckedItem(R.id.nav_home);
        }
        //default checked nav is home !

        getUserProfile();
        getProviderData();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) { //meaning the state when user has logged out
                    Intent i = new Intent(AccountActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menu) {
        switch(menu.getItemId()){
            case R.id.nav_home:getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,new HomeFragment())
                    .commit();
                break;
            case R.id.nav_personal:getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,new HomeFragment())
                    .commit();
                break;
            case R.id.nav_shared:getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,new SharedFragment())
                    .commit();
                break;
            case R.id.nav_chat:getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,new ChatFragment())
                    .commit();
                break;
            case R.id.nav_settings:getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,new SettingsFragment())
                    .commit();
                break;
            case R.id.nav_about:getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment,new AboutFragment())
                    .commit();
                break;
            case R.id.nav_logout://logout
                showProgressDialogWithTitle(DIALOGMESSAGE);
                if (account != null) {
                    mAuth.signOut();
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Toast.makeText(AccountActivity.this, "Logged out successfully",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Google SignOut Successful");
                        }
                    });
                } else {
                    mAuth.signOut();
                    Toast.makeText(AccountActivity.this, "Logged out successfully",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);//close drawer after selection
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth.addAuthStateListener(mAuthListener);
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }

    }


    // Method to show Progress bar
    private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }


    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName().toString();
            String email = user.getEmail().toString();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified or not
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            //String uid = user.getUid();
            displayusername.setText(name);
            displayemail.setText(email);
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.profileicon)
                    .error(R.drawable.profileicon)
                    .into(displayimage);
        }
        // [END get_user_profile]
    }

    public void getProviderData() {
        // [START get_provider_data]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();

                displayusername.setText(name);
                displayemail.setText(email);
                Picasso.get()
                        .load(photoUrl)
                        .placeholder(R.drawable.profileicon)
                        .error(R.drawable.profileicon)
                        .into(displayimage);

            }
        }
        // [END get_provider_data]
    }



}
