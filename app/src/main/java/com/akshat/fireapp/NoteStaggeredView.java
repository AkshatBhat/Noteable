package com.akshat.fireapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NoteStaggeredView extends AppCompatActivity {

    private static final String TAG = "NoteStaggeredView";
    private static final int NUM_COLUMNS = 2;
    private static final int ADD_NOTE_REQUEST = 1;
    // private ArrayList<String> mImageUrls = new ArrayList<>();
    // private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> contents = new ArrayList<>();
    private ArrayList<String> contentsDisplay = new ArrayList<>();
    private List<NewNoteActivity> noteList = new ArrayList<>();
    private Context mainContext;
    private FloatingActionButton fab_add_note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_staggered_view);


        fab_add_note = (FloatingActionButton) findViewById(R.id.add_note);
        fab_add_note.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses fab_add_note
                Intent i;

                //case (R.id.name_widget) : i = new Intent(mContext, NewNoteAcitivity.class); mContext.startActivity(i); break;
                i = new Intent(NoteStaggeredView.this, NewNoteActivity.class);
                startActivityForResult(i, ADD_NOTE_REQUEST);
            }
        });


        // Collections.reverse(mNames);



        //staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            String title = data.getStringExtra(NewNoteActivity.EXTRA_TITLE);
            String content = data.getStringExtra(NewNoteActivity.EXTRA_DESCRIPTION);
            String contentDisplay = data.getStringExtra(NewNoteActivity.EXTRA_DESCRIPTIONDISPLAY);

            titles.add(0,title);
            contents.add(0,content);
            contentsDisplay.add(0,contentDisplay);

//            Collections.reverse(titles);
//            Collections.reverse(contents);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);

            StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                    // new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls);
                    new StaggeredRecyclerViewAdapter(this,titles,contentsDisplay);

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setAdapter(staggeredRecyclerViewAdapter);
            //recyclerView.setHasFixedSize(true);
        }
    }
}
















