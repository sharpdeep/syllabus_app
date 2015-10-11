package com.example.stu_nwad.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stu_nwad.syllabus.Homework;
import com.example.stu_nwad.syllabus.HomeworkHandler;
import com.example.stu_nwad.syllabus.HomeworkPullTask;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HomeworkHandler, View.OnClickListener{

    public static final String[] HISTORY_TYPES = {"Homework", "Discussion"};

    private ArrayAdapter<String> data_adapter;

    private Spinner history_type_spinner;
    private Button query_history_button;
    private EditText history_content;
    private EditText history_count_edit;


    private void find_views(){
        history_type_spinner = (Spinner) findViewById(R.id.history_type_spinner);
        query_history_button = (Button) findViewById(R.id.query_history_button);
        history_content = (EditText) findViewById(R.id.history_content);
        history_count_edit = (EditText) findViewById(R.id.history_count_edit);
    }

    private void setup_views(){
        data_adapter =  new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                HISTORY_TYPES);
        history_type_spinner.setAdapter(data_adapter);

        // 读取额外信息
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        int index = -1;
        for(int i = 0 ; i < HISTORY_TYPES.length ; ++i){
            if (type.equals(HISTORY_TYPES[i])){
                index = i;
                break;
            }
        }
        if (index != -1)
            history_type_spinner.setSelection(index);

        // 添加监听器
        query_history_button.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        find_views();
        setup_views();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void query_history(){

        if (history_count_edit.getText().toString().isEmpty()) {
            Toast.makeText(HistoryActivity.this, "少年，要输入查询的数目吖~~~~", Toast.LENGTH_SHORT).show();
            return;
        }
        int count = Integer.parseInt(history_count_edit.getText().toString());

        // Homework
        if (history_type_spinner.getSelectedItem().toString().equals(HISTORY_TYPES[0])){
            HomeworkPullTask task = new HomeworkPullTask(this, this);
            Lesson lesson = ClassDialog.lesson;
            task.get_homework(count, lesson.id, lesson.start_year, lesson.end_year, lesson.semester);
        }
    }

    @Override
    public void deal_with_homework(ArrayList<Homework> all_homework) {
        if (all_homework == null){
            history_content.setText("None");
            Toast.makeText(HistoryActivity.this, "没有作业的历史信息呢", Toast.LENGTH_SHORT).show();
            return ;
        }
        StringBuilder sb = new StringBuilder();
        for(Homework homework : all_homework)
            sb.append(homework.toString() + "\n\n");
        history_content.setText(sb.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query_history_button:
                query_history();
                break;
        }
    }
}
