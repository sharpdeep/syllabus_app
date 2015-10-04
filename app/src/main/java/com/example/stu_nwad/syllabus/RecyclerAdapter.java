package com.example.stu_nwad.syllabus;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by STU_nwad on 2015/9/23.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Object[] mDataset;
    private SyllabusActivity syllabusActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(Object[] myDataset, SyllabusActivity syllabusActivity) {
        this.mDataset = myDataset;
        this.syllabusActivity = syllabusActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((TextView) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset[position] instanceof Lesson){
            holder.mTextView.setOnClickListener(new ClickAndShow(position));
        }
        holder.mTextView.setText(mDataset[position].toString());
        holder.mTextView.setClickable(true);
        holder.mTextView.setTextSize(8);
        holder.mTextView.setTextColor(Color.WHITE);
        holder.mTextView.setGravity(Gravity.CENTER);

    }

    class ClickAndShow implements View.OnClickListener{

        private int position = -1;

        public ClickAndShow(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(syllabusActivity, "Clicked " + position, Toast.LENGTH_SHORT).show();
            if (position != -1 && mDataset[position] instanceof Lesson){
                syllabusActivity.showClassInfo((Lesson) mDataset[position]);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}