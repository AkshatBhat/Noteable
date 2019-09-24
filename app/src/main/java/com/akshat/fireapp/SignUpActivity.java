package com.akshat.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText email;
    private EditText password;
    private TextView login;
    private Button signupbutton;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signupbutton = (Button) findViewById(R.id.signupbutton);
        login = (TextView) findViewById(R.id.redirect_to_login);

        SpannableString ss = new SpannableString(login.getText());
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(cs,0,login.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(ss);
        login.setMovementMethod(LinkMovementMethod.getInstance());


        signupbutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        startLogin();
                    }
                }
        );

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) { //meaning the state when user has logged in
                    Intent i = new Intent(SignUpActivity.this, AccountActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void startLogin () {
        String Email = email.getText().toString();
        String Password = password.getText().toString();

        if (TextUtils.isEmpty(Email)) {
            email.setError("Enter Email!");
            email.requestFocus();
        }

        else if(!isValidEmail(email.getText().toString())) {
            email.setError("Enter Valid Email!");
            email.requestFocus();
        }

        else if(TextUtils.isEmpty(Password)){
            password.setError("Enter Password!");
            password.requestFocus();
        }

        else if(TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password)){
            Toast.makeText(SignUpActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
        }

        else if(!(TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password))){
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this.getApplicationContext(), "Sign Up Unsuccessful! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Signed Up successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        else {
            Toast.makeText(SignUpActivity.this, "Unknown Error Occurred!", Toast.LENGTH_SHORT).show();
        }

    }





    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



}
