package com.example.stu_nwad.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import com.example.stu_nwad.syllabus.R;

/**
 * Created by STU_nwad on 2015/10/7.
 */
public class MyTabActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_host_layout);

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("个人").setIndicator("personal").setContent(R.id.personal_layout));
        tabHost.addTab(tabHost.newTabSpec("作业").setIndicator("homework").setContent(R.id.homework_layout));
        tabHost.addTab(tabHost.newTabSpec("吹水啦").setIndicator("discuss").setContent(R.id.talk_layout));

    }

}
