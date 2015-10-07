package com.example.stu_nwad.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.stu_nwad.adapters.ListViewAdapter;
import com.example.stu_nwad.syllabus.ClassParser;
import com.example.stu_nwad.syllabus.FileOperation;
import com.example.stu_nwad.syllabus.HttpCommunication;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



public class MainActivity extends AppCompatActivity {
    public static Object[] weekdays_syllabus_data;     // 用于向显示课表的activity传递数据
    public static ArrayList<Lesson> weekends_syllabus_data;
    public static String info_about_syllabus;
    public static final String USERNAME_FILE = "username.txt";
    public static final String PASSWORD_FILE = "password.txt";

    // 控件及常量
    public static final String TAG = "POSTTEST";
    public static  String[] YEARS;// = {"2012-2013", "2013-2014", "2014-2015", "2015-2016", "2016-2017", "2017-2018"};
    public static final String[] SEMESTER = {"SPRING", "SUMMER", "AUTUMN"};

    private int position = -1;  // 用于决定保存的文件名
    private String semester;    // 用于决定保存的文件名


    private EditText address_edit;  // 服务器地址
    private EditText username_edit;
    private EditText passwd_edit;
    private ListView syllabus_list_view;    // 用于显示所有课表的listview

    // 创建主界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 加载主布局
        YEARS = generate_years(5);  // 生成5年的选项
        getAllViews();
        setupViews();
    }


    // 产生近count年的年份字符串 2015-2016
    public static String[] generate_years(int count){
        // 获取当今年份
        int cur_year = Calendar.getInstance().get(Calendar.YEAR);
        // 生成count年的年份数据
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
        syllabus_list_view = (ListView) findViewById(R.id.syllabus_list_view);
    }

    private void setupViews(){
        ListViewAdapter list_apapter = new ListViewAdapter(this);
        syllabus_list_view.setAdapter(list_apapter);

        // 读取用户
        String[] user = FileOperation.load_user(this, USERNAME_FILE, PASSWORD_FILE);
        if (user != null){
            username_edit.setText(user[0]);
            passwd_edit.setText(user[1]);
        }else{
//            Toast.makeText(MainActivity.this, "用户文件不存在哟", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "用户文件不存在");
        }

        // debug
        TextView about_text = (TextView) findViewById(R.id.about_text_box);
        about_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tab = new Intent(MainActivity.this, MyTabActivity.class);
                startActivity(tab);
            }
        });
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

        if (id == R.id.delete_action) {
            delete_cached_files();
            Toast.makeText(MainActivity.this, "已经清空所有缓存文件", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void delete_cached_files(){
        String filename = null;
        String username = username_edit.getText().toString();
        // 删除全部缓存文件
        for(int i = 0 ; i < YEARS.length ; ++ i){
            for(int j = 0 ; j < SEMESTER.length ; ++j){
                filename = FileOperation.generate_syllabus_file_name(username, YEARS[i], SEMESTER[j], "_");
                Log.d(TAG, "deleting " + filename);
                if (FileOperation.delete_file(this, filename))
                    Log.d(TAG, "deleted " + filename );
                else
                    Log.d(TAG, "delete[failed]" + filename);
            }
        }
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
                Log.d(TAG, "maybe there is a typo in submit(int, int)");
                break;
        }
        info_about_syllabus = username + " " + years + " " + semester;
        // 先判断有无之前保存的文件
//        String filename = username + "_" + years + "_" + semester;
        String filename = FileOperation.generate_syllabus_file_name(username, years, semester, "_");
        String json_data = FileOperation.read_from_file(MainActivity.this, filename);
        if (json_data != null) {
            syllabusGetter.apply_json(json_data);
            return;
        }


        String passwd = passwd_edit.getText().toString();
//            {"SPRING", "SUMMER", "AUTUMN"}
        String semester_code = "";
        if (semester.equals("SPRING"))
            semester_code = "2";
        else if (semester.equals("SUMMER"))
            semester_code = "3";
        else if (semester.equals("AUTUMN"))
            semester_code = "1";

        HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("username", username);
        postData.put("password", passwd);
        postData.put("submit", "query");
        postData.put("years", years);
        postData.put("semester", semester_code);
        Log.d(TAG, "onClick");
        syllabusGetter.execute(postData);
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

        /**
         * 将json数据进行解析
         * @param json_data
         */
            public void apply_json(String json_data){
                onPostExecute(json_data);
            }

            @Override
            protected void onPostExecute(String response){
                if (response.isEmpty()){
                    Toast.makeText(MainActivity.this, "没能成功连接到服务器呢~~~~重试一下吧~~~~", Toast.LENGTH_SHORT).show();
                    return;
                }
                parse_and_display(response);

            }

            private void parse_and_display(String json_data){
                if (classParser.parseJSON(json_data)) {
                    classParser.inflateTable();     // 用数据填充课表
                    MainActivity.weekdays_syllabus_data = classParser.weekdays_syllabus_data;
                    MainActivity.weekends_syllabus_data = classParser.weekend_classes;
                    Log.d(TAG, "established adapter");
                    Intent syllabus_activity = new Intent(MainActivity.this, SyllabusActivity.class);
                    startActivity(syllabus_activity);
//                    Toast.makeText(MainActivity.this, "读取课表成功哟~~~~", Toast.LENGTH_SHORT).show();

                    // 保存文件 命名格式: name_years_semester
                    String username = ((EditText) MainActivity.this.findViewById(R.id.username_edit)).getText().toString();
//                    String filename = username + "_" + YEARS[position] + "_"
//                            + semester;
                    String filename = FileOperation.generate_syllabus_file_name(username, YEARS[position], semester, "_");
                    if (FileOperation.save_to_file(MainActivity.this, filename, json_data)){
//                        Toast.makeText(MainActivity.this, "成功保存文件 " + filename, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "saved file " + filename);
                    }
                    // 保存用户文件
                    FileOperation.save_user(MainActivity.this, USERNAME_FILE, PASSWORD_FILE, username, passwd_edit.getText().toString());
                }
            }


        }
}
