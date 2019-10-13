package com.akshat.fireapp;


import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class NewNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "codingwithmitch.com.recyclerviewstaggered.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "codingwithmitch.com.recyclerviewstaggered.EXTRA_DESCRIPTION";
    public static final String EXTRA_DESCRIPTIONDISPLAY = "codingwithmitch.com.recyclerviewstaggered.EXTRA_DESCRIPTIONDISPLAY";

    private Button btnCreate;
    private EditText etTitle,etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        btnCreate = (Button) findViewById(R.id.new_note_btn);
        etTitle = (EditText) findViewById(R.id.new_note_title);
        etContent = (EditText) findViewById(R.id.new_note_content);

        btnCreate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                String title =removeTrailingSpaces(etTitle.getText().toString());
                String content =removeTrailingSpaces(etContent.getText().toString());

                String contentDisplay;
                if(content.length()<=30)
                {
                    contentDisplay = content;
                }
                else
                {
                    contentDisplay = content.substring(0,30);
                }

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content))
                {
                    Toast.makeText(NewNoteActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show(); return;
                }

                Intent data = new Intent();
                data.putExtra(EXTRA_TITLE,title);
                data.putExtra(EXTRA_DESCRIPTION,content);
                data.putExtra(EXTRA_DESCRIPTIONDISPLAY,contentDisplay);

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



}