package com.akshat.fireapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NoteStaggeredView extends AppCompatActivity {

    private static final String TAG = "NoteStaggeredView";
    private static final int NUM_COLUMNS = 2;
    private static final int ADD_NOTE_REQUEST = 1;

    public static final String EXTRA_TITLE = "codingwithmitch.com.recyclerviewstaggered.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "codingwithmitch.com.recyclerviewstaggered.EXTRA_CONTENT";
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> contents = new ArrayList<>();
    private ArrayList<String> contentsDisplay = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private static final String DIALOGMESSAGE = "Loading ...";
    private List<Upload> noteObjectList = new ArrayList<>();

    private FloatingActionButton fab_add_note;
    private RecyclerView recyclerView;
    //private ChildEventListener noteListener;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageTask mUploadTask;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private FirebaseRecyclerAdapter adapter;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    /*private String contentOfURL;
    private String urlString;
    private String TextHolder = "" , TextHolder2 = "";
    private URL url;
    private BufferedReader bufferReader;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_staggered_home);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(user.getDisplayName());
        mStorageRef = FirebaseStorage.getInstance().getReference(user.getDisplayName());
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.RecyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        Query query = mDatabaseRef;

        FirebaseRecyclerOptions<Upload> options =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(query, new SnapshotParser<Upload>() {
                            @NonNull
                            @Override
                            public Upload parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Upload upload = snapshot.getValue(Upload.class);
                                String title = upload.mName;
                                String textFileUrl = upload.mTextFileUrl;
                                String date = upload.mDate;
                                return new Upload(title, textFileUrl, date);
                            }
                        })
                        .build();


        adapter = new FirebaseRecyclerAdapter<Upload, SViewHolder>(options) {
            @NonNull
            @Override
            public SViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
                return new SViewHolder(view);
            }

            @NonNull
            @Override
            public Upload getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            public void updateOptions(@NonNull FirebaseRecyclerOptions<Upload> options) {
                super.updateOptions(options);
            }

            @Override
            public void onDataChanged() {
                recyclerView.removeAllViews();
                super.onDataChanged();
            }

            @Override
            protected void onBindViewHolder(@NonNull SViewHolder holder, int position, @NonNull Upload model) {
                Log.d(TAG, "onBindViewHolder: called.");

                holder.notetitle.setText(model.getName());

                //final String urlString = model.getmTextFileUrl();
                String urlString = model.getmTextFileUrl();
                final String titlePut = model.getName();
                final String datePut = model.getmDate();
                //new GetNotePadFileFromServer().execute();
                String TextHolder = "", TextHolder2 = "", contentDisplay = "";
                try {
                    URL url = new URL(urlString);

                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(url.openStream()));

                    while ((TextHolder2 = bufferReader.readLine()) != null) {

                        TextHolder = TextHolder + TextHolder2 + "\n";
                    }
                    bufferReader.close();

                } catch (MalformedURLException malformedURLException) {

                    malformedURLException.printStackTrace();
                    TextHolder = malformedURLException.toString();

                } catch (IOException iOException) {

                    iOException.printStackTrace();
                    TextHolder = iOException.toString();
                }


//                new Thread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        try
//                        {
//
//                            URL url = new URL(urlString);//my app link change it
//
//                            HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
//                            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//                            StringBuilder lin2 = new StringBuilder();
//                            String line;
//
//                            while ((line = br.readLine()) != null)
//                            {
//                                lin2.append(line);
//                            }
//
//                            contentOfURL = lin2.toString();
//                            Log.d("texts", "onClick: "+lin2);
//                        } catch (IOException e)
//                        {
//                            Log.d("texts", "onClick: "+e.getLocalizedMessage());
//                            e.printStackTrace();
//                        }
//                    }
//
//                }).start();
                final String contentPut = TextHolder;
                if (TextHolder.length() <= 65) {
                    contentDisplay = TextHolder;
                } else {
                    contentDisplay = TextHolder.substring(0, 65) + "..." + "\n";
                }
                holder.notecontent.setText(contentDisplay);

                holder.notedate.setText(model.getmDate());
                holder.notecontent.setText(contentDisplay);

                holder.notedate.setText(model.getmDate());

                holder.notecontent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Clicked on content!");
                        //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();

                        Intent i;
                        i = new Intent(getApplicationContext(), EditNoteActivity.class);
                        i.putExtra(EXTRA_TITLE, titlePut);
                        i.putExtra(EXTRA_CONTENT, contentPut);
                        startActivity(i);
                        finish();
                    }
                });
                holder.notetitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Clicked on title!");
                        //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                        Intent i;
                        i = new Intent(getApplicationContext(), EditNoteActivity.class);
                        i.putExtra(EXTRA_TITLE, titlePut);
                        i.putExtra(EXTRA_CONTENT, contentPut);
                        startActivity(i);
                        finish();
                    }
                });
                holder.notetitle.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        // Toast.makeText(NoteStaggeredView.this, "Long clicked title", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "longClicked on title!");
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);

                        sendIntent.putExtra(Intent.EXTRA_TEXT,"Title: "+titlePut+"\n\nContent:\n"+contentPut+"\nLast modified: "+datePut+
                                "\nSent from Noteable");
                        sendIntent.setType("text/plain");
                        sendIntent.setType("text/enriched<bold></bold>");
                        sendIntent.putExtra(Intent.EXTRA_TEXT,"\nSent via Noteable.");
                        sendIntent.setType("text/plain");
                        // Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(Intent.createChooser(sendIntent, null));
                        Toast.makeText(NoteStaggeredView.this, "Note shared successfully.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                holder.notecontent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.d(TAG, "longClicked on content!");
                        //  Toast.makeText(NoteStaggeredView.this, "l", Toast.LENGTH_SHORT).show();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,"Title: "+titlePut+"\n\nContent:\n"+contentPut+"\nLast modified: "+datePut+
                                "\nSent via Noteable.");
                        sendIntent.setType("text/plain");

                        //  Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(Intent.createChooser(sendIntent, null));
                        Toast.makeText(NoteStaggeredView.this, "Note shared successfully.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                if (position == getItemCount() - 1)
                    hideProgressDialogWithTitle();

            }
        };

        recyclerView.setAdapter(adapter);


        fab_add_note = findViewById(R.id.add_note);
        fab_add_note.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses fab_add_note
                Intent i;

                //case (R.id.name_widget) : i = new Intent(mContext, NewNoteAcitivity.class); mContext.startActivity(i); break;
                i = new Intent(NoteStaggeredView.this, NewNoteActivity.class);
                startActivity(i);
            }
        });

        /*noteListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Upload upload = dataSnapshot.getValue(Upload.class);
                noteObjectList.add(0,upload);
                staggeredRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };*/

        /*  noteListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Upload upload = dataSnapshot.getValue(Upload.class);
                noteObjectList.add(0,upload);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };*/

//        mDatabaseRef.addChildEventListener(noteListener);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        ArrayList<NoteUtil> notes = Utilities.getAllSavedNotes(this);
//
//        if(notes!=null || notes.size() == 0)
//        {
//            Toast.makeText(this, "No saved notes!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            String title = data.getStringExtra(NewNoteActivity.EXTRA_TITLE);
            String content = data.getStringExtra(NewNoteActivity.EXTRA_DESCRIPTION);
            String contentDisplay = data.getStringExtra(NewNoteActivity.EXTRA_DESCRIPTIONDISPLAY);
            String date = data.getStringExtra(NewNoteActivity.EXTRA_DATE);

            titles.add(0,title);
            contents.add(0,content);
            contentsDisplay.add(0,contentDisplay);
            dates.add(0,date);

            StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                    // new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls);
                    new StaggeredRecyclerViewAdapter(this,titles,contentsDisplay,dates);

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setAdapter(staggeredRecyclerViewAdapter);
            //recyclerView.setHasFixedSize(true);
        }
    }*/

    public static class SViewHolder extends RecyclerView.ViewHolder {

        TextView notetitle, notecontent, notedate;

        public SViewHolder(View itemView) {
            super(itemView);
            this.notetitle = itemView.findViewById(R.id.notetitle);
            this.notecontent = itemView.findViewById(R.id.notedesc);
            this.notedate = itemView.findViewById(R.id.notedate);
        }
    }

    @Override
    protected void onStart() {
        showProgressDialogWithTitle(DIALOGMESSAGE);
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


//    public class GetNotePadFileFromServer extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            try {
//                url = new URL(urlString);
//
//                bufferReader = new BufferedReader(new InputStreamReader(url.openStream()));
//
//                while ((TextHolder2 = bufferReader.readLine()) != null) {
//
//                    TextHolder += TextHolder2;
//                }
//                bufferReader.close();
//
//            } catch (MalformedURLException malformedURLException) {
//
//
//                malformedURLException.printStackTrace();
//                TextHolder = malformedURLException.toString();
//
//            } catch (IOException iOException) {
//
//
//                iOException.printStackTrace();
//
//                TextHolder = iOException.toString();
//            }
//
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(Void finalTextHolder) {
//
//            //textView.setText(TextHolder);
//            contentOfURL = TextHolder;
//            super.onPostExecute(finalTextHolder);
//        }
//
//    }
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


}
















