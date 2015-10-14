package com.example.stu_nwad.activities;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stu_nwad.adapters.RecyclerAdapter;
import com.example.stu_nwad.helpers.FileOperation;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;


public class SyllabusActivity extends AppCompatActivity {

    public static Lesson clicked_lesson;

    public static final String DEFAULT_SYLLABUS_FILE = "default_syllabus";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView weekend_text;
    private TextView info_text;

    private void setupViews(){
        // 设置 RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new GridLayoutManager(this, 6, RecyclerView.VERTICAL, false);  // 不管周末的课程先
        GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new RecyclerAdapter(MainActivity.weekdays_syllabus_data, this);
        mRecyclerView.setAdapter(mAdapter);

        // 提示区域
        info_text = (TextView) findViewById(R.id.message_text);
        info_text.setText("点击课程: 备忘录|作业信息分享|吹水");

        // 显示周末的信息
        weekend_text = (TextView) findViewById(R.id.weekend_syllabus_text);
        String text = "";
        if (MainActivity.weekends_syllabus_data.size() != 0){
            for(Lesson lesson : MainActivity.weekends_syllabus_data)
                text += lesson.toText() + "\n";
        }else{
            text = "周末课程信息: 周末没有课";
        }
        weekend_text.setText(text);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);
        setupViews();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(MainActivity.info_about_syllabus);
    }

    public void showClassInfo(Lesson lesson){
//        if (dialog == null)
//            dialog = new ClassDialog(this, R.style.ClassDialog, lesson);
//        else
//            dialog.setLesson(lesson);
//
//        dialog.show();
        clicked_lesson = lesson;
        Intent tab_intent = new Intent(this, MyTabActivity.class);
        startActivity(tab_intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.set_default_syllabus:
                if (set_default_syllabus()){
                    Toast.makeText(SyllabusActivity.this, "成功设置默认课表~~~~", Toast.LENGTH_SHORT).show();
                    return true;
                }else
                    return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 设置默认学期
     */
    private boolean set_default_syllabus(){
        String syllabus_file_name =  FileOperation.generate_syllabus_file_name(MainActivity.cur_username, MainActivity.cur_year_string,
                MainActivity.cur_semester, "_");
        return FileOperation.save_to_file(this, DEFAULT_SYLLABUS_FILE, syllabus_file_name);

    }


}
