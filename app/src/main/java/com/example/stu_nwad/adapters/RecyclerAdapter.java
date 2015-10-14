package com.example.stu_nwad.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.activities.SyllabusActivity;

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
//        android.R.layout.simple_list_item_1
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        // set the view's size, margins, paddings and layout parameters
//        TextView text_view = (TextView) v.findViewById(R.id.class_grid_text);

        ViewHolder vh = new ViewHolder((TextView) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset[position] instanceof Lesson){
//            Log.d("GRID_VIEW", "[" + position + "]" + (position / ClassParser.COLUMNS) + "行" + (position % ClassParser.COLUMNS) + "列");
            holder.mTextView.setOnClickListener(new ClickAndShow(position));
//            holder.mTextView.setBackgroundResource(R.drawable.input_box);

        }
// else{
//            holder.mTextView.setBackgroundResource(0);
//        }
        holder.mTextView.setText(mDataset[position].toString());
        holder.mTextView.setClickable(true);
        holder.mTextView.setTextSize(12);
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
                // 因为安卓会重用view, 所以这里需要注意判断一下，点击的到底是不是真正的课程
                TextView view = (TextView) v;
                String text = view.getText().toString();
                if (text.length() <= 1) // 即为 上课时间1-C 或者 星期数 或者是空字符
                    return;
                // 这里以后才是真的课程哟~
                syllabusActivity.showClassInfo((Lesson) mDataset[position]);
//                Log.d("GRID_VIEW", "[" + position + "]" + "  " + (position / ClassParser.COLUMNS) + "行" + " " + (position % ClassParser.COLUMNS) + "列");
//                Toast.makeText(syllabusActivity, "[" + position + "]" + "  " + (position / ClassParser.COLUMNS) + "行" + " " + (position % ClassParser.COLUMNS) + "列", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}