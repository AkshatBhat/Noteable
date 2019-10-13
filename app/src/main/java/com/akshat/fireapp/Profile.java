package com.akshat.fireapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private EditText profilename;
    private static final String TAG = "Profile";
    private Button updateprofilebutton;
    private ImageView displayimage;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilename = (EditText)findViewById(R.id.name);
        updateprofilebutton = (Button)findViewById(R.id.updateprofile);
        displayimage = (ImageView)findViewById(R.id.profile_image);

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Uploads");

        updateprofilebutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        if(mUploadTask!=null && mUploadTask.isInProgress())
                        {
                            Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_LONG).show();
                        }
                        else{
                            uploadFile();
                        }
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
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==PICK_IMAGE && data.getData()!=null && data!=null)
        {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.profileicon)
                    .error(R.drawable.profileicon)
                    .into(displayimage);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Problem Occured", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateProfile(Uri imageUrl){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(profilename.getText().toString())
                .setPhotoUri(imageUrl)
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


    //func for giving the file extension .something
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    //for actually uploading file
    private void uploadFile(){
        if(imageUri!=null)
        {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            mUploadTask = fileReference.putFile(imageUri)
                     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {

                                     Toast.makeText(Profile.this,"Upload Successful",Toast.LENGTH_SHORT).show();
//                                     Upload upload = new Upload(profilename.getText().toString().trim(),taskSnapshot.getUploadSessionUri().toString());
//                                     String uploadId = mDatabaseRef.push().getKey();
                                    // mDatabaseRef.child(uploadId).setValue(upload);
                                     updateProfile(uri);

                                 }
                             });
                         }
                     })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        //@Override
//                        /*public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                            mProgressBar.setProgress((int)progress);
//                        }*/
//                    });
        }
        else
        {
            Toast.makeText(Profile.this,"No file selected",Toast.LENGTH_SHORT).show();
        }
    }

}
