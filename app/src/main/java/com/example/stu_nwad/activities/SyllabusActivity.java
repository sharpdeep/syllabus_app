package com.example.stu_nwad.activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.stu_nwad.adapters.RecyclerAdapter;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;


public class SyllabusActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView weekend_text;
    private TextView info_text;
    private ClassDialog dialog;

    private void setupViews(){
        // 设置 RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new GridLayoutManager(this, 6, RecyclerView.VERTICAL, false);  // 不管周末的课程先
        GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new RecyclerAdapter(MainActivity.weekdays_syllabus_data, this);
        mRecyclerView.setAdapter(mAdapter);

        // 显示周末的信息
        weekend_text = (TextView) findViewById(R.id.weekend_syllabus_text);
        String text = "";
        if (MainActivity.weekends_syllabus_data.size() != 0){
            for(Lesson lesson : MainActivity.weekends_syllabus_data)
                text += lesson.toText() + "\n";
        }else{
            text = "周末没课哟，出去浪吧~~~~";
        }
        weekend_text.setText(text);

        // 提示区域
        info_text = (TextView) findViewById(R.id.message_text);
        info_text.setText(MainActivity.info_about_syllabus + " 点击课程可以添加备注信息哟~~");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        setupViews();

    }

    public void showClassInfo(Lesson lesson){
        if (dialog == null)
            dialog = new ClassDialog(this, R.style.ClassDialog);
        dialog.setLesson(lesson);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}
