package com.akshat.fireapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "StaggeredRecyclerViewAd";

    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mContents = new ArrayList<>();
    private ArrayList<String> mDates = new ArrayList<>();
    //private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;
    //private List<NewNoteActivity> notes = new ArrayList<>();

    //public StaggeredRecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls)
    public StaggeredRecyclerViewAdapter(Context context, ArrayList<String> titles,ArrayList<String> contents,ArrayList<String> dates){
        mTitles = titles;
        mContents = contents;
        //  mImageUrls = imageUrls;
        mContext = context;
        mDates = dates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        //NewNoteActivity currentNote = notes.get(position);


        holder.notedate.setText(mDates.get(position));
        holder.notetitle.setText(mTitles.get(position));
        holder.notecontent.setText(mContents.get(position));
        /*holder.notedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + mNames.get(position));
                //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                Intent i;
                i = new Intent(mContext, NewNoteActivity.class);
                mContext.startActivity(i);
            }
        });*/
        holder.notecontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + mNames.get(position));
                //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                Intent i;
                i = new Intent(mContext, NewNoteActivity.class);
                mContext.startActivity(i);
            }
        });
        holder.notetitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + mNames.get(position));
                //Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                Intent i;
                i = new Intent(mContext, NewNoteActivity.class);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        // return mNames.size();
        return mTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

            TextView notetitle,notecontent,notedate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.notetitle = itemView.findViewById(R.id.notetitle);
            this.notecontent = itemView.findViewById(R.id.notedesc);
            this.notedate = itemView.findViewById(R.id.notedate);
        }
    }
}
