package com.example.stu_nwad.userecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // 控件及常量

    public static final String TAG = "POSTTEST";
    public static final String EMPTY_CLASS_STRING = "NONE";
    public static final String[] LABELS = {"一", "二", "三", "四", "五", "六", "日"};

    public static final String[] YEARS = {"2012-2013", "2013-2014", "2014-2015", "2015-2016", "2016-2017", "2017-2018"};
    public static final String[] SEMESTER = {"SPRING", "SUMMER", "AUTUMN"};


    private EditText address_edit;
    private EditText username_edit;
    private EditText passwd_edit;
    private Button submit_button;
    private TextView result_text_view;
    private Spinner years_spin_box;
    private Spinner semester_spin_box;

    private void getAllViews(){
        address_edit = (EditText) findViewById(R.id.address_edit);
        username_edit = (EditText) findViewById(R.id.username_edit);
        passwd_edit = (EditText) findViewById(R.id.passwd_edit);
        submit_button = (Button) findViewById(R.id.submit_button);
        result_text_view = (TextView) findViewById(R.id.result_text_view);
        years_spin_box = (Spinner) findViewById(R.id.year_spin_box);
        semester_spin_box = (Spinner) findViewById(R.id.semester_spin_box);
    }

    private void setupViews(){
        ArrayAdapter<String> years = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, YEARS);
        ArrayAdapter<String> semesters = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SEMESTER);
        years_spin_box.setAdapter(years);
        semester_spin_box.setAdapter(semesters);
        years_spin_box.setSelection(3);     // 2015-2016
        semester_spin_box.setSelection(2);  // AUTUMN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 8, RecyclerView.VERTICAL, false);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
        mRecyclerView.setLayoutManager(gridLayoutManager);

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {

            String username = username_edit.getText().toString();
            String requestURL = address_edit.getText().toString();
            PostEvent postEvent = new PostEvent(result_text_view, requestURL);
            // 先判断有无之前保存的文件
            try {
                String filename = username + "_" + MainActivity.this.years_spin_box.getSelectedItem().toString() + "_"
                        + MainActivity.this.semester_spin_box.getSelectedItem().toString();
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
                postEvent.display(json_data);
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String passwd = passwd_edit.getText().toString();
            String years = years_spin_box.getSelectedItem().toString();
            String semester = semester_spin_box.getSelectedItem().toString();
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
            postEvent.execute(postData);
        }
    }

        class PostEvent extends AsyncTask<HashMap<String, String>, Void, String> {

            private TextView view;
            private String requestURL;

            private ArrayList<Lesson> all_classes;

            public PostEvent(TextView view, String requestURL){
                super();
                this.view = view;
                this.requestURL = requestURL;
            }

            @Override
            protected String doInBackground(HashMap<String, String>... params) {
                Log.d(TAG, "Doing now");
                String  response = performPostCall(requestURL, params[0]);
                return response;
            }

            public void display(String json_data){
                onPostExecute(json_data);
            }

            public boolean parseJSON(String json_data){
                // 用response作为json传给 JSONTOkener
                JSONTokener jsonParser = new JSONTokener(json_data);
                ArrayList<Lesson> all_classes = new ArrayList<Lesson>();
                try {
                    JSONObject curriculum = (JSONObject) jsonParser.nextValue();
                    // 得到所有课程的数组
                    JSONArray classes = curriculum.getJSONArray("classes");
                    Log.d(TAG, classes.length() + " classes");
                    for (int i = 0; i < classes.length(); ++i) {
                        // 得到每一节课
                        JSONObject lesson = (JSONObject) classes.get(i);
                        // 处理每一节课的信息
                        String name = lesson.getString("name");
                        Log.d(TAG, name);
                        String id = lesson.getString("id");
                        Log.d(TAG, id);
                        String teacher = lesson.getString("teacher");
                        Log.d(TAG, teacher);
                        String room = lesson.getString("room");
                        Log.d(TAG, room);
                        String duration = lesson.getString("duration");
                        Log.d(TAG, duration);

                        Lesson cls = new Lesson();
                        cls.name = name;
                        cls.id = id;
                        cls.teacher = teacher;
                        cls.room = room;
                        cls.duration = duration;

                        // 得到一周之内要上课的日期以及具体上课时间
                        JSONObject days = lesson.getJSONObject("days");
                        final int weekdays = 7;
                        HashMap<String, String> lesson_days = new HashMap<String, String>();
                        for (int j = 0; j < weekdays; ++j) {
                            String key = "w" + j;
                            String isNull = days.getString(key);
                            if (!isNull.equals("None"))     // 去除没有的天数
                                lesson_days.put(key, days.getString(key));
                            Log.d(TAG, key + ":" + days.getString(key));
                        }
                        cls.days = lesson_days;

                        all_classes.add(cls);
                    }
                    this.all_classes = all_classes;
                    return true;
                }catch (JSONException e) {
                    Log.d(TAG, e.toString());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(String response){
                if (parseJSON(response)) {
                    LessonAdapter adapter = new LessonAdapter(MainActivity.this);
                    Object [] objs = adapter.objs;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0 ; i < adapter.objs.length ; ++i){
                        if (adapter.objs[i] != null){
                            stringBuilder.append(adapter.objs[i].toString() + "\n");
                        }
                    }
                    Log.d(TAG, stringBuilder.toString());
                    Log.d(TAG, "established adapter");
                    mAdapter = new MyAdapter(objs);
                    mRecyclerView.setAdapter(mAdapter);

                    // 保存文件 命名格式: name_years_semester
                    String username = ((EditText) MainActivity.this.findViewById(R.id.username_edit)).getText().toString();
                    String filename = username + "_" + MainActivity.this.years_spin_box.getSelectedItem().toString() + "_"
                            + MainActivity.this.semester_spin_box.getSelectedItem().toString();
                    try{
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
                }
            }

            class LessonAdapter extends BaseAdapter {


                Object[] objs = new Object[14 * 8];

                private Context context;

                private void init(){

                    Log.d(TAG, "start init()");

                    for(int i = 0 ; i < objs.length ; ++i)
                        objs[i] = EMPTY_CLASS_STRING;   // 初始化数据

                    // 处理非课程的数据
                    for (int i = 0 ; i < objs.length ; ++i){
                        // 处理星期几这些日期
                        if (i <= 7){
                            if (i == 0)
                                objs[i] = "";   // 空白的一个格子
                            else
                                objs[i] = LABELS[i - 1];    // 转化为中文的数字

                        }else if (i % 8 == 0){
                            // 处理第一列的 课的节数
                            if (i / 8 <= 9) {   // 这里还是用数字表示
                                int num = i / 8;
                                objs[i] = num + "";
                            }else{
                                // 用ABC代替
                                String label = "";
                                switch (i / 8){
                                    case 10:
                                        label = "0";
                                        break;
                                    case 11:
                                        label = "A";
                                        break;
                                    case 12:
                                        label = "B";
                                        break;
                                    case 13:
                                        label = "C";
                                        break;
                                    default:
                                        break;
                                }
                                objs[i] = label;
                            }
                        }else{
                            objs[i] = EMPTY_CLASS_STRING; // 置为空
                        }
                    }
                    Log.d(TAG, "before inflate class_table");

                    // 填充课表数据
                    for(int i = 0 ; i < all_classes.size() ; ++i){
                        // 遍历key set
                        Lesson lesson = all_classes.get(i);
                        for (String key : lesson.days.keySet()){
                            // key 的值是  w1 w2 这种格式
                            String class_time = lesson.days.get(key);
                            Log.d(TAG, "class_time " + class_time);
                            if (!class_time.equals(EMPTY_CLASS_STRING)){
                                // 添加到obj数组中
                                int offset = Integer.parseInt( key.substring(1));   // 得到 w1 中的数字部分
                                if (offset == 0)
                                    offset = 7;     // 因为web api返回的数据 w0 是代表周日
                                boolean hasBeenAdded = false;
                                for(int count = 0 ; count < class_time.length() ; ++count){

                                    char c = class_time.charAt(count);  // 得到数据
                                    int row = -1;
                                    switch (c){
                                        case '0':
                                            row = 10;
                                            break;
                                        case 'A':
                                            row = 11;
                                            break;
                                        case 'B':
                                            row = 12;
                                            break;
                                        case 'C':
                                            row = 13;
                                            break;
                                        case '单':   // 跳过这个字符
                                        case '双':
                                            hasBeenAdded = false;
                                            break;
                                        default:
                                            row = c - '0';
                                            break;
                                    }
                                    int index = row * 8 + offset;
                                    if (row == -1)   // 说明是单双周的情况
                                        continue;
                                    if (!hasBeenAdded) {     // 一节课添加一次即可
                                        objs[index] = lesson;   // 将这节课添加到合适的位置
                                        hasBeenAdded = true;
                                    }else{
                                        objs[index] = "同上";
                                    }

                                }
                            }
                        }
                    }
                    Log.d(TAG, "end init()");
                }

                public LessonAdapter(Context context){
                    super();
                    this.context = context;
                    init();
                }

                @Override
                public int getCount() {
                    return 0;
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    return null;
                }
            }

            public String  performPostCall(String requestURL,
                                           HashMap<String, String> postDataParams) {
                URL url;
                String response = "";
                try {
                    url = new URL(requestURL);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(1500);
                    conn.setConnectTimeout(1500);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    Log.d(TAG, "start writing data");
                    writer.write(getPostDataString(postDataParams));
                    Log.d(TAG, "writer.write()");
                    writer.flush();
                    Log.d(TAG, "writer.flush()");
                    writer.close();
                    Log.d(TAG, "writer.close()");
                    os.close();
                    Log.d(TAG, "outputstream has closed!");
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line=br.readLine()) != null) {
                            response+=line;
                        }
                        Log.d(TAG, "POST CALL OK");
                    }
                    else {
                        response="";
                        Log.d(TAG, "POST CALL BAD");

                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

                return response;
            }

            private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
                Log.d(TAG, "getPostDataString");
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for(Map.Entry<String, String> entry : params.entrySet()){
                    if (first)
                        first = false;
                    else
                        result.append("&");

                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }

                return result.toString();
            }
        }
}
