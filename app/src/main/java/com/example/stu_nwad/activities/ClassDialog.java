package com.example.stu_nwad.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stu_nwad.syllabus.FileOperation;
import com.example.stu_nwad.syllabus.Homework;
import com.example.stu_nwad.syllabus.HomeworkHandler;
import com.example.stu_nwad.syllabus.HomeworkParser;
import com.example.stu_nwad.syllabus.HomeworkPullTask;
import com.example.stu_nwad.syllabus.HttpCommunication;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/5.
 */
public class ClassDialog extends Dialog implements View.OnClickListener, HomeworkHandler{

    public static Lesson lesson;

    Context context;

    // 个人备注区域
    private TextView class_info_text_view;
    private Button submit_button;
    private EditText personal_comment_area;
    private String personal_comment = "";

    // 作业区
    private EditText last_homework;
    private EditText homework_content_edit;
    private EditText homework_time_edit;
    private Button homework_submit_button;
    private Button homework_history_button;

    // 吹水区
    private ListView discussion_list_view;
    private Button submit_discussion_button;
    private EditText discussion_content_edit;

    private TabHost tabHost;
    private TabHost.TabSpec personal_tab_content;
    private TabHost.TabSpec homework_tab_content;
    private TabHost.TabSpec discuss_tab_content;



    public ClassDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ClassDialog(Context context, int themeId, Lesson lesson){
        super(context, themeId);
        this.context = context;
        this.lesson = lesson;
        // 碰触到外面不会dismiss
        setCanceledOnTouchOutside(false);

    }

    public void setLesson(Lesson lesson){
        this.lesson = lesson;
//        Log.d(MainActivity.TAG, lesson.representation());
        class_info_text_view.setText(lesson.representation());
        personal_comment = load_comment();
        if (personal_comment != null) {
            personal_comment_area.setText(personal_comment);
            personal_comment_area.setSelection(personal_comment.length());
        }else{
            personal_comment_area.setText("");
        }
        // 每次设置这个的时候应该把之前的数据先清空
        homework_content_edit.setText("");
        homework_time_edit.setText("");
//        last_homework.setVisibility(View.INVISIBLE);

        // 当调用这个函数的时候就拉取一次最新的消息
        get_latest_homework(1);
    }


    private void find_views(){
        class_info_text_view = (TextView) findViewById(R.id.dialog_content);
        submit_button = (Button) tabHost.findViewById(R.id.personal_submit);
        personal_comment_area = (EditText) tabHost.findViewById(R.id.personal_note);

        homework_time_edit = (EditText) tabHost.findViewById(R.id.homework_time_edit);
        homework_content_edit = (EditText) tabHost.findViewById(R.id.homework_content_edit);
        homework_submit_button = (Button) tabHost.findViewById(R.id.homework_submit_button);
        homework_history_button = (Button) tabHost.findViewById(R.id.homework_history_button);
        last_homework = (EditText) tabHost.findViewById(R.id.last_homework);

        discussion_list_view = (ListView) tabHost.findViewById(R.id.discuss_list_view);
        discussion_content_edit = (EditText) tabHost.findViewById(R.id.talk_field);
        submit_discussion_button = (Button) tabHost.findViewById(R.id.submit_discussion_button);
    }

    private void setup_views(){

        setContentView(R.layout.dialog_layout);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        personal_tab_content = tabHost.newTabSpec("personal").setIndicator("个人").setContent(R.id.personal_layout);
        tabHost.addTab(personal_tab_content);

        homework_tab_content = tabHost.newTabSpec("homework").setIndicator("作业").setContent(R.id.homework_layout);
        tabHost.addTab(homework_tab_content);

        discuss_tab_content = tabHost.newTabSpec("discuss").setIndicator("吹水").setContent(R.id.talk_layout);
        tabHost.addTab(discuss_tab_content);

        find_views();

        // add listeners
        submit_button.setOnClickListener(this);
        homework_submit_button.setOnClickListener(this);
        homework_history_button.setOnClickListener(this);
        submit_discussion_button.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setup_views();
        setLesson(lesson);

    }

    @Override
    public void show(){
        // show 之后才调用 onCreate的
        super.show();
    }

    private boolean save_comment(){
        if (personal_comment_area.getText().toString().isEmpty())
            return false;
        // 内容没有改变
        if (personal_comment_area.getText().toString().equals(personal_comment))
            return false;
        String info = MainActivity.info_about_syllabus;
        String username = info.split(" ")[0];
        String filename = FileOperation.generate_class_file_name(username, lesson.id, "_");
        Log.d(MainActivity.TAG, "saving file" + filename);
        return FileOperation.save_to_file(context, filename, personal_comment_area.getText().toString());
    }

    private String load_comment(){
        String info = MainActivity.info_about_syllabus;
        String username = info.split(" ")[0];
        String filename = FileOperation.generate_class_file_name(username, lesson.id, "_");
        Log.d(MainActivity.TAG, "loading " + filename);
        return FileOperation.read_from_file(context, filename);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 个人信息
            case R.id.personal_submit:
                save_comment();
                dismiss();
                break;

            // 作业信息
            case R.id.homework_submit_button:
                add_homework_to_database();
                break;

            // 作业历史
            case R.id.homework_history_button:
                show_history_activity(HistoryActivity.HISTORY_TYPES[0]);
                break;

            // 添加讨论信息到相应课程
            case R.id.submit_discussion_button:
                add_discussion_to_database();
                break;
        }
    }

    private void show_history_activity(String type){
        Intent history_intent = new Intent(context, HistoryActivity.class);
        history_intent.putExtra("type", type);
        context.startActivity(history_intent);
    }


    private void get_latest_homework(int count){
        HomeworkPullTask get_homework_task = new HomeworkPullTask(context, this);
        get_homework_task.get_homework(count, lesson.id, lesson.start_year, lesson.end_year, lesson.semester);
    }

    private void add_lesson_to_database(){
        // def __init__(self, number, name, credit, teacher, room, span, time_, start_year, end_year, semester):
        HashMap<String, String> data = new HashMap<>();
        data.put("number", lesson.id);
        data.put("name", lesson.name);
        data.put("credit", lesson.credit);
        data.put("teacher", lesson.teacher);
        data.put("room", lesson.room);
        data.put("span", lesson.duration);
        data.put("time", "None");
        data.put("start_year", lesson.start_year + "");
        data.put("end_year", lesson.end_year + "");
        data.put("semester", lesson.semester + "");

        // 添加课程
        InsertTask insert_class_task = new InsertTask(context.getString(R.string.insert_class_api));
        insert_class_task.execute(data);

    }

    private void add_user_to_database(){
        HashMap<String, String> data = new HashMap<>();
        // 用户名
        data.put("username", MainActivity.cur_username);
        InsertTask insert_user_task = new InsertTask(context.getString(R.string.insert_user_api));
        insert_user_task.execute(data);
    }

    private void add_homework_to_database(){
        HashMap<String, String> data = new HashMap<>();
        // 对应到具体的课程
        data.put("number", lesson.id);
        data.put("start_year", lesson.start_year + "");
        data.put("end_year", lesson.end_year + "");
        data.put("semester", lesson.semester + "");

        // 作业的信息
        data.put("publisher", MainActivity.cur_username);
        long timestamp =  (System.currentTimeMillis() / 1000);
        data.put("pub_time", timestamp + "");  // 现在的时间
        data.put("hand_in_time", homework_time_edit.getText().toString());
        data.put("content", homework_content_edit.getText().toString().trim()); // 去除没必要的空白字符

        InsertTask insert_homework_task = new InsertTask(context.getString(R.string.insert_home_work_api));
        insert_homework_task.execute(data);

    }

//    self.parser.add_argument("publisher", required=True)
//            self.parser.add_argument("pub_time", required=True, type=float)
//            self.parser.add_argument("content", required=True)

    public void add_discussion_to_database(){
        HashMap<String, String> data = new HashMap<>();

        // 对应到具体的课程
        data.put("number", lesson.id);
        data.put("start_year", lesson.start_year + "");
        data.put("end_year", lesson.end_year + "");
        data.put("semester", lesson.semester + "");

        // 讨论的信息
        data.put("publisher", MainActivity.cur_username);
        long timestamp = (System.currentTimeMillis() / 1000);
        data.put("pub_time", timestamp + "");
        data.put("content", discussion_content_edit.getText().toString());

        InsertDiscussionTask task = new InsertDiscussionTask(context.getString(R.string.insert_discussion_api));
        task.execute(data);
    }

    @Override
    public void deal_with_homework(ArrayList<Homework> all_homework) {
        if (all_homework == null) {
            Log.d(MainActivity.TAG, "看起来好像all_homework是null呢.....");
            return;
        }

        // 显示最新的作业
        Homework latest = all_homework.get(all_homework.size() - 1);    // 最新发布的作业
        last_homework.setText(latest.toString());
    }

    /**
     * 用于完成 需要 POST 的网络任务
     */
    class InsertTask extends AsyncTask<HashMap<String, String> , Void, String> {

        private String address;

        public InsertTask(String addr){
            this.address = addr;
        }

        public void setAddress(String addr){
            this.address = addr;
        }



        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            return HttpCommunication.performPostCall(this.address, params[0]);
        }

        @Override
        protected void onPostExecute(String response){
            if (response.isEmpty()){
                Toast.makeText(context, "error connection", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONTokener json_parser = new JSONTokener(response);
            try {
                JSONObject json_obj = (JSONObject) json_parser.nextValue();
                if (json_obj.has(HomeworkParser.ERROR_STRING)){
                    String error_string = json_obj.getString(HomeworkParser.ERROR_STRING);
                    String NO_SUCH_CLASS = "no such class";
                    String NO_SUCH_USER = "no such user";
                    // 这是第一个检查的元素
                    if (error_string.equals(NO_SUCH_CLASS)){
                        // 说明要添加这节课到数据库中
                        add_lesson_to_database();
                    }else if (error_string.equals(NO_SUCH_USER)){
                        // 说明要添加用户到数据库中
                        add_user_to_database();
                    }
                    // 再试一次 应该就ok了
                    add_homework_to_database();
                }else{
                    Toast.makeText(context, "作业信息分享成功哟~~~~", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 用于插入评论
     */
    class InsertDiscussionTask extends AsyncTask<HashMap<String, String>, Void, String>{

        private String address;

        public InsertDiscussionTask(String addr){
            this.address = addr;
        }

        public void setAddress(String addr){
            this.address = addr;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            return HttpCommunication.performPostCall(this.address, params[0]);
        }

        @Override
        protected void onPostExecute(String response){
            discussion_content_edit.setText(response);
        }
    }
}
