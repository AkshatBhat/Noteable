package com.akshat.fireapp;


import android.content.ContentResolver;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class NewNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "codingwithmitch.com.recyclerviewstaggered.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "codingwithmitch.com.recyclerviewstaggered.EXTRA_DESCRIPTION";
    public static final String EXTRA_DESCRIPTIONDISPLAY = "codingwithmitch.com.recyclerviewstaggered.EXTRA_DESCRIPTIONDISPLAY";
    public static final String EXTRA_DATE = "codingwithmitch.com.recyclerviewstaggered.EXTRA_DATE";

    private Button btnCreate;
    private EditText etTitle,etContent;
    private String etDate;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageTask mUploadTask;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        btnCreate = (Button) findViewById(R.id.new_note_btn);
        etTitle = (EditText) findViewById(R.id.new_note_title);
        etContent = (EditText) findViewById(R.id.new_note_content);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        btnCreate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                String title =removeTrailingSpaces(etTitle.getText().toString());
                String content =removeTrailingSpaces(etContent.getText().toString());
                NoteUtil note = new NoteUtil(System.currentTimeMillis(),title,content);
                String contentDisplay;
                etDate = note.getDateTimeFormatted(getApplicationContext());
                if(content.length()<=50)
                {
                    contentDisplay = content;
                }
                else
                {
                    contentDisplay = content.substring(0,50);
                }

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content))
                {
                    Toast.makeText(NewNoteActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show(); return;
                }

                //saveNote(title,content);

                if(mUploadTask!=null && mUploadTask.isInProgress())
                {
                    Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_LONG).show();
                }
                else{
                    putNoteToStorage(title,content,etDate);
                }

                Intent data = new Intent();
                data.putExtra(EXTRA_TITLE,title);
                data.putExtra(EXTRA_DESCRIPTION,content);
                data.putExtra(EXTRA_DESCRIPTIONDISPLAY,contentDisplay);
                data.putExtra(EXTRA_DATE,etDate);

                setResult(RESULT_OK, data);
                finish();
            }

            public String removeTrailingSpaces(String param)
            {
                if (param == null)
                    return null;
                int len = param.length();
                for (; len > 0; len--) {
                    if (!Character.isWhitespace(param.charAt(len - 1)))
                        break;
                }
                return param.substring(0, len);
            }

        });
    }

//    private void saveNote(String title,String content)
//    {
//        NoteUtil note = new NoteUtil(System.currentTimeMillis(),title,content);
//        if(Utilities.saveNote(getApplicationContext(),note))
//        {
//            Toast.makeText(NewNoteActivity.this, "Your Note is saved!", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(NewNoteActivity.this, "Cannot save as less storage space!", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void putNoteToStorage(final String title, final String content, final String date)
    {
        if(user!=null) {
            mStorageRef = FirebaseStorage.getInstance().getReference(user.getDisplayName());
            final StorageReference fileReference = mStorageRef.child(title+"."+"txt");

            mDatabaseRef = FirebaseDatabase.getInstance().getReference(user.getDisplayName());

            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                fos = openFileOutput(title+".txt", MODE_PRIVATE);//context communicates with the Android API
                fos.write(content.getBytes());
                Toast.makeText(getApplicationContext(), "Saved to " + getFilesDir() + "/" + title + ".txt", Toast.LENGTH_SHORT).show();
                fis = openFileInput(title+".txt");
                mUploadTask = fileReference.putStream(fis)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Upload upload = new Upload(title,taskSnapshot.getUploadSessionUri().toString(),date);
                                        mDatabaseRef.child(title).setValue(upload);
                                        Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally
            {
                if(fos!=null)
                {
                    try{
                        fos.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Error Occured: User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    //func for giving the file extension .something
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

}