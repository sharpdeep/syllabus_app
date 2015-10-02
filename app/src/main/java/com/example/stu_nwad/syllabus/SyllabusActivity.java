package com.example.stu_nwad.syllabus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SyllabusActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView weekend_text;

    private void setupViews(){
        // 设置 RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new GridLayoutManager(this, 6, RecyclerView.VERTICAL, false);  // 不管周末的课程先
        GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new MyAdapter(MainActivity.weekdays_syllabus_data);
        mRecyclerView.setAdapter(mAdapter);

        // 显示周末的信息
        weekend_text = (TextView) findViewById(R.id.weekend_syllabus_text);
        String text = "";
        if (MainActivity.weekends_syllabus_data.size() != 0){
            for(Lesson lesson : MainActivity.weekends_syllabus_data)
                text += lesson.representation() + "\n";
        }else{
            text = "周末没课哟，出去浪吧~~~~";
        }
        weekend_text.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        setupViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
