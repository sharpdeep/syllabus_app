package com.example.stu_nwad.activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.stu_nwad.helpers.UpdateHelper;
import com.example.stu_nwad.interfaces.UpdateHandler;
import com.example.stu_nwad.parsers.ClassParser;
import com.example.stu_nwad.helpers.FileOperation;
import com.example.stu_nwad.helpers.HttpCommunication;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;
import com.example.stu_nwad.syllabus.SyllabusVersion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements UpdateHandler {
    public static Object[] weekdays_syllabus_data;     // 用于向显示课表的activity传递数据
    public static ArrayList<Lesson> weekends_syllabus_data;
    public static String info_about_syllabus;
    public static final String USERNAME_FILE = "username.txt";
    public static final String PASSWORD_FILE = "password.txt";


    // 用于和其他activity共享的数据
    public static String cur_year_string;
    public static int cur_semester;
    public static String cur_username;

    // 控件及常量
    public static final String TAG = "POSTTEST";
    public static  String[] YEARS;// = {"2012-2013", "2013-2014", "2014-2015", "2015-2016", "2016-2017", "2017-2018"};
    public static final String[] SEMESTER = {"SPRING", "SUMMER", "AUTUMN"};

    private int position = -1;  // 用于决定保存的文件名
    private String semester;    // 用于决定保存的文件名


//    private EditText address_edit;  // 服务器地址
    private EditText username_edit;
    private EditText passwd_edit;
    private ListView syllabus_list_view;    // 用于显示所有课表的listview

    // 如果已经显示过默认课表就没必要再显示了
    private boolean has_showed_default = false;
    private boolean has_checked_update = false;

    private UpdateHelper updateHelper;

    // 创建主界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 加载主布局
        YEARS = generate_years(4);  // 生成5年的选项
        getAllViews();
        setupViews();
        if (!has_showed_default)
            load_default_syllabus();
        // 检查更新
        if (!has_checked_update)
            check_update();

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
//        address_edit = (EditText) findViewById(R.id.address_edit);
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

        // show my words
        TextView about_text = (TextView) findViewById(R.id.about_text_box);
        about_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(about);
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
        int id = item.getItemId();

        if (id == R.id.check_update_action){
            Intent update_activity = new Intent(this, UpdateActivity.class);
            startActivity(update_activity);
            return true;
        }

        if (id == R.id.delete_default_syllabus){
            if (delete_default_syllabus())
                Toast.makeText(MainActivity.this, "清除了默认课表的设置", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "清除默认课表出错", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean delete_default_syllabus(){
        if (FileOperation.hasFile(this, SyllabusActivity.DEFAULT_SYLLABUS_FILE))
            return FileOperation.delete_file(this, SyllabusActivity.DEFAULT_SYLLABUS_FILE);
        return true;
    }

    private void load_default_syllabus(){
        String default_file_name = FileOperation.read_from_file(this, SyllabusActivity.DEFAULT_SYLLABUS_FILE);
        if (default_file_name != null){
            if (FileOperation.hasFile(this, default_file_name)){
                String json_data = FileOperation.read_from_file(this, default_file_name);
                if (json_data != null){
                    SyllabusGetter syllabusGetter = new SyllabusGetter(getString(R.string.server_address));
                    // 设置一些相关信息
                    String[] info = default_file_name.split("_");
                    cur_username = info[0];
                    cur_year_string = info[1];
                    cur_semester = FileOperation.semester_to_int(info[2]);
                    for(int i = 0 ; i < YEARS.length ; ++i)
                        if (cur_year_string.equals(YEARS[i]))
                            position = i;
                    info_about_syllabus = cur_username + " " + cur_year_string + " " + info[2];
                    has_showed_default = true;
                    syllabusGetter.apply_json(json_data);
                    return;
                }
            }
        }
    }

    private void submit(int position, int view_id){
        this.position = position;
        String username = username_edit.getText().toString();
        cur_username = username;
//        String requestURL = address_edit.getText().toString();
        String requestURL = this.getString(R.string.server_address);
        SyllabusGetter syllabusGetter = new SyllabusGetter(requestURL);

        String years = YEARS[position];  // 点击到列表的哪一项
        cur_year_string = years;    // 用于共享目的
        semester = null;
        switch (view_id){
            case R.id.spring_text_view:
                semester = "SPRING";
                cur_semester = 2;
                break;
            case R.id.summer_text_view:
                semester = "SUMMER";
                cur_semester = 3;
                break;
            case R.id.autumn_text_view:
                semester = "AUTUMN";
                cur_semester = 1;
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

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("password", passwd);
        postData.put("submit", "query");
        postData.put("years", years);
        postData.put("semester", semester_code);
        Log.d(TAG, "onClick");
        syllabusGetter.execute(postData);
    }

    private void check_update(){
        if (updateHelper == null)
            updateHelper = new UpdateHelper(this, this);
        updateHelper.check_for_update();
        has_checked_update = true;
    }


    @Override
    public void deal_with_update(int flag, final SyllabusVersion version) {
        if (flag == UpdateHandler.EXIST_UPDATE){
            // 存在更新的话
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("发现新版本, 是否更新?");
            builder.setMessage("描述:\n" + version.description);
            builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Intent update_activity = new Intent(MainActivity.this, UpdateActivity.class);
//                    startActivity(update_activity);
                    updateHelper.download(version.dowload_address, version);
//                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("稍后", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
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

    public LongTimeClickListener getOnLongClickListener(int position){
        return new LongTimeClickListener(position);
    }

    public class LongTimeClickListener implements View.OnLongClickListener{

        private int position;

        public LongTimeClickListener(int pos){
            this.position = pos;
        }

        private void delete_cache_file(Context context, String file_name){
            if (FileOperation.hasFile(context, file_name)){
                if (FileOperation.delete_file(context, file_name))
                    Toast.makeText(context, "成功删除缓存文件", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "删除缓存文件失败", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, "不存在该缓存文件", Toast.LENGTH_SHORT).show();

        }

        @Override
        public boolean onLongClick(View v) {
            final int id = v.getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("清除缓存文件");
            TextView text = (TextView) v;
            builder.setMessage("清除 " + YEARS[position] + " " + text.getText().toString().replace("\n", "") + " 课表?");
            builder.setPositiveButton("清除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(MainActivity.this, "清除缓存文件", Toast.LENGTH_SHORT).show();
                    String username = username_edit.getText().toString();
                    String semester = FileOperation.semester_from_view_id(id);
                    String file_name = FileOperation.generate_syllabus_file_name(username, YEARS[position], semester, "_");
                    delete_cache_file(MainActivity.this, file_name);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return true;
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
                return HttpCommunication.performPostCall(requestURL, params[0]);
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
                    String err_msg = "吖!没能连接到服务器呢~重试一下" + "\n" + "请确保接入学校内网哟~\n"
                            + "学分制系统服务器可能关了" + "\n宿舍服务器可能没开呢~" + "\n再重试一下试试看~";
                    Toast.makeText(MainActivity.this, err_msg, Toast.LENGTH_LONG).show();
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
