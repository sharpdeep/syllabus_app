package com.example.stu_nwad.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.stu_nwad.syllabus.R;

public class HistoryActivity extends AppCompatActivity {

    public static final String[] HISTORY_TYPES = {"Homework", "Discussion"};

    private ArrayAdapter<String> data_adapter;

    private Spinner history_type_spinner;


    private void find_views(){
        history_type_spinner = (Spinner) findViewById(R.id.history_type_spinner);
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
}
