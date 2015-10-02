package com.example.stu_nwad.syllabus;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.stu_nwad.adapters.ListViewAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Object[] weekdays_syllabus_data;     // 用于向显示课表的activity传递数据
    public static ArrayList<Lesson> weekends_syllabus_data;
    public static String info_about_syllabus;

    // 控件及常量
    public static final String TAG = "POSTTEST";
    public static  String[] YEARS;// = {"2012-2013", "2013-2014", "2014-2015", "2015-2016", "2016-2017", "2017-2018"};
    public static final String[] SEMESTER = {"SPRING", "SUMMER", "AUTUMN"};

    private int position = -1;  // 用于决定保存的文件名
    private String semester;    // 用于决定保存的文件名


    private EditText address_edit;  // 服务器地址
    private EditText username_edit;
    private EditText passwd_edit;
    private Button submit_button;
    private Spinner years_spin_box; // 年份选择
    private Spinner semester_spin_box;  // 学期选择
    private ListView syllabus_list_view;    // 用于显示所有课表的listview



    // 产生近count年的年份字符串 2015-2016
    public static String[] generate_years(int count){
        // 获取当今年份
        int cur_year = Calendar.getInstance().get(Calendar.YEAR);
        // 生成四年的年份数据
        String[] strs = new String[count];
        for(int i = 0 ; i < strs.length ; ++ i){
            strs[i] = (cur_year - i) + "-" + (cur_year - i + 1);
        }
        return strs;
    }

    private void getAllViews(){
        address_edit = (EditText) findViewById(R.id.address_edit);
        username_edit = (EditText) findViewById(R.id.username_edit);
        passwd_edit = (EditText) findViewById(R.id.passwd_edit);
        submit_button = (Button) findViewById(R.id.submit_button);
        years_spin_box = (Spinner) findViewById(R.id.year_spin_box);
        semester_spin_box = (Spinner) findViewById(R.id.semester_spin_box);
        syllabus_list_view = (ListView) findViewById(R.id.syllabus_list_view);
    }

    private void setupViews(){
        ArrayAdapter<String> years = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, YEARS);
        ArrayAdapter<String> semesters = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SEMESTER);
        years_spin_box.setAdapter(years);
        semester_spin_box.setAdapter(semesters);
//        years_spin_box.setSelection(0);     // 2015-2016 今明两年咯 哈哈
        semester_spin_box.setSelection(2);  // AUTUMN
        ListViewAdapter list_apapter = new ListViewAdapter(this);

        syllabus_list_view.setAdapter(list_apapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 加载主布局
        YEARS = generate_years(4);  // 生成4年的选项
        getAllViews();
        setupViews();
        submit_button.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void submit(int position, int view_id){
        this.position = position;
        String username = username_edit.getText().toString();
        String requestURL = address_edit.getText().toString();
        SyllabusGetter syllabusGetter = new SyllabusGetter(requestURL);

        String years = YEARS[position];  // 点击到列表的哪一项
        semester = null;
        switch (view_id){
            case R.id.spring_text_view:
                semester = "SPRING";
                break;
            case R.id.summer_text_view:
                semester = "SUMMER";
                break;
            case R.id.autumn_text_view:
                semester = "AUTUMN";
                break;
            default:
                Log.d(TAG, "maybe ther is a typo in submit(int, int)");
                break;
        }
        info_about_syllabus = years + " " + semester;
        // 先判断有无之前保存的文件
        try {
            String filename = username + "_" + years + "_"
                    + semester;
            FileInputStream inStream = openFileInput(filename);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length= -1 ;
            while((length=inStream.read(buffer))!=-1)   {
                stream.write(buffer,0,length);  // 写入字节流中
            }
            stream.close();
            inStream.close();
            String json_data = stream.toString();
            syllabusGetter.display(json_data);
//            Toast.makeText(MainActivity.this, "之前缓存的数据呢~~~", Toast.LENGTH_SHORT).show();
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        String passwd = passwd_edit.getText().toString();
//            {"SPRING", "SUMMER", "AUTUMN"}
        if (semester.equals("SPRING"))
            semester = "2";
        else if (semester.equals("SUMMER"))
            semester = "3";
        else if (semester.equals("AUTUMN"))
            semester = "1";

        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("username", username);
        postData.put("password", passwd);
        postData.put("submit", "query");
        postData.put("years", years);
        postData.put("semester", semester);
        Log.d(TAG, "onClick");
        syllabusGetter.execute(postData);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.submit_button:
                Toast.makeText(MainActivity.this, "请点击下面列出来的课程表进行查看课表", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }


    // 获取内部类的实例
    public ShowSyllabus getOnClickListener(int position){
        return new ShowSyllabus(position);
    }

    public class ShowSyllabus implements View.OnClickListener{
        private int position; // 保存了点击的项是在列表中的哪一行
        
        public ShowSyllabus(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "正在获取 " + YEARS[position] + " " + ((TextView)v).getText().toString(), Toast.LENGTH_SHORT).show();
            submit(position, v.getId());
        }
    }

    /**
     * 用于异步发送网络请求
     */
        class SyllabusGetter extends AsyncTask<HashMap<String, String>, Void, String> {

            private String requestURL;
            private ClassParser classParser; // = new ClassParser();

            public SyllabusGetter(String requestURL){
                super();
                this.requestURL = requestURL;
                classParser = new ClassParser(MainActivity.this);
            }

            @Override
            protected String doInBackground(HashMap<String, String>... params) {
                Log.d(TAG, "Doing now");
                String  response = HttpCommunication.performPostCall(requestURL, params[0]);
                return response;
            }

            public void display(String json_data){
                onPostExecute(json_data);
            }

            @Override
            protected void onPostExecute(String response){
                if (response.isEmpty()){
                    Toast.makeText(MainActivity.this, "没能成功连接到服务器呢~~~~重试一下吧~~~~", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (classParser.parseJSON(response)) {
                    classParser.inflateTable();     // 用数据填充课表
                    MainActivity.weekdays_syllabus_data = classParser.weekdays_syllabus_data;
                    MainActivity.weekends_syllabus_data = classParser.weekend_classes;
                    Log.d(TAG, "established adapter");
                    Intent syllabus_activity = new Intent(MainActivity.this, SyllabusActivity.class);
                    startActivity(syllabus_activity);
//                    Toast.makeText(MainActivity.this, "读取课表成功哟~~~~", Toast.LENGTH_SHORT).show();

                    // 保存文件 命名格式: name_years_semester
                    String username = ((EditText) MainActivity.this.findViewById(R.id.username_edit)).getText().toString();
                    String filename = username + "_" + YEARS[position] + "_"
                            + semester;
                    try{
//                        Toast.makeText(MainActivity.this, filename, Toast.LENGTH_SHORT).show();
                        FileOutputStream out = openFileOutput(filename, Context.MODE_PRIVATE);
                        out.write(response.getBytes("UTF-8"));
                        out.flush();
                        out.close();
                    }catch (FileNotFoundException e){
                        Log.d(TAG, e.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 否则的话就是传进来的json数据无法解析
                }
            }
        }
}
