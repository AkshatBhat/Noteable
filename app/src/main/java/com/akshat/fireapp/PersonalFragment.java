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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class PersonalFragment extends Fragment {
    //declarations
    private static final String TAG = "NoteStaggeredView";
    private static final int NUM_COLUMNS = 2;
    private static final int ADD_NOTE_REQUEST = 1;
    public static final String EXTRA_TITLE = "codingwithmitch.com.recyclerviewstaggered.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "codingwithmitch.com.recyclerviewstaggered.EXTRA_CONTENT";
    private static final String DIALOGMESSAGE = "Loading ...";

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> contents = new ArrayList<>();
    private ArrayList<String> contentsDisplay = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();

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
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staggered_home,container,false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(getContext());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(user.getDisplayName());
        mStorageRef = FirebaseStorage.getInstance().getReference(user.getDisplayName());

        recyclerView = view.findViewById(R.id.RecyclerView);
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
                                return new Upload(title,textFileUrl,date);
                            }
                        })
                        .build();


        adapter = new FirebaseRecyclerAdapter<Upload, NoteStaggeredView.SViewHolder>(options) {
            @NonNull
            @Override
            public NoteStaggeredView.SViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
                return new NoteStaggeredView.SViewHolder(view);
            }

            @NonNull
            @Override
            public Upload getItem(int position) {
                return super.getItem(getItemCount()-1-position);
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
            protected void onBindViewHolder(@NonNull NoteStaggeredView.SViewHolder holder, int position, @NonNull Upload model) {
                Log.d(TAG, "onBindViewHolder: called.");

                holder.notetitle.setText(model.getName());

                String urlString = model.getmTextFileUrl();
                final String titlePut = model.getName();

                String TextHolder="",TextHolder2="",contentDisplay="";
                try {
                    URL url = new URL(urlString);

                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(url.openStream()));

                    while ((TextHolder2 = bufferReader.readLine()) != null) {

                        TextHolder = TextHolder + TextHolder2 +"\n";
                    }
                    bufferReader.close();

                } catch (MalformedURLException malformedURLException) {

                    malformedURLException.printStackTrace();
                    TextHolder = malformedURLException.toString();

                } catch (IOException iOException) {

                    iOException.printStackTrace();
                    TextHolder = iOException.toString();
                }
                final String contentPut = TextHolder;
                if(TextHolder.length()<=65)
                {
                    contentDisplay = TextHolder;
                }
                else
                {
                    contentDisplay = TextHolder.substring(0,65) + "..." + "\n";
                }
                holder.notecontent.setText(contentDisplay);

                holder.notedate.setText(model.getmDate());

                holder.notecontent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Clicked on content!");
                        //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();

                        Intent i;
                        i = new Intent(getContext(), EditNoteActivity.class);
                        i.putExtra(EXTRA_TITLE, titlePut);
                        i.putExtra(EXTRA_CONTENT, contentPut);
                        startActivity(i);

                    }
                });
                holder.notetitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Clicked on title!");
                        //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                        Intent i;
                        i = new Intent(getContext(), EditNoteActivity.class);
                        i.putExtra(EXTRA_TITLE, titlePut);
                        i.putExtra(EXTRA_CONTENT, contentPut);
                        startActivity(i);

                    }
                });
                if (position == getItemCount() - 1)
                    hideProgressDialogWithTitle();
            }
        };
//        Toast.makeText(getContext(), "Please wait ...", Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);


        fab_add_note = view.findViewById(R.id.add_note);
        fab_add_note.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses fab_add_note
                Intent i;

                //case (R.id.name_widget) : i = new Intent(mContext, NewNoteAcitivity.class); mContext.startActivity(i); break;
                i = new Intent(getActivity(), NewNoteActivity.class);
                startActivity(i);
            }
        });

        return view;
    }


    public static class SViewHolder extends RecyclerView.ViewHolder{

        TextView notetitle,notecontent,notedate;

        public SViewHolder(View itemView) {
            super(itemView);
            this.notetitle = itemView.findViewById(R.id.notetitle);
            this.notecontent = itemView.findViewById(R.id.notedesc);
            this.notedate = itemView.findViewById(R.id.notedate);
        }
    }

    @Override
    public void onStart() {
        showProgressDialogWithTitle(DIALOGMESSAGE);
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
        Toast.makeText(getContext(), "Please wait ...", Toast.LENGTH_SHORT).show();
    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        Toast.makeText(getContext(), "Just a few more sec ...", Toast.LENGTH_SHORT).show();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
        super.onPause();
    }
}


