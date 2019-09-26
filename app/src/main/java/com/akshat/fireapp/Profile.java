package com.akshat.fireapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Profile extends AppCompatActivity {

    private EditText profilename;
    private static final String TAG = "Profile";
    private Button updateprofilebutton;
    private ImageView displayimage;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilename = (EditText)findViewById(R.id.name);
        updateprofilebutton = (Button)findViewById(R.id.updateprofile);
        displayimage = (ImageView)findViewById(R.id.profile_image);

        updateprofilebutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        updateProfile();
                    }
                }
        );

        displayimage.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        pickImageFromGallery();
                    }
                }
        );
    }


    private void pickImageFromGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==PICK_IMAGE);
        {
            imageUri = data.getData();
            displayimage.setImageURI(imageUri);
        }
        if(resultCode==RESULT_CANCELED && requestCode==PICK_IMAGE)
        {
            Toast.makeText(getApplicationContext(), "Problem Occured", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(profilename.getText().toString())
                .setPhotoUri(imageUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(getApplicationContext(), "User Profile Updated", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            Log.d(TAG, "User profile updating failed.");
                        }
                    }
                });
    }
}
