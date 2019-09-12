package com.akshat.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {

    private Button logoutbutton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private static final String TAG = "GoogleSignInTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();

        logoutbutton = (Button) findViewById(R.id.log_out_button);

        logoutbutton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        if (account != null) {
                            mAuth.signOut();
                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    Toast.makeText(AccountActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,"Google SignOut Successful");

                                }});
                        }

                        else {
                            mAuth.signOut();
                            Toast.makeText(AccountActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) { //meaning the state when user has logged out
                    Intent i = new Intent(AccountActivity.this, MainActivity.class);
                    //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    public void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth.addAuthStateListener(mAuthListener);
    }

    /*private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
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

    }*/


}
