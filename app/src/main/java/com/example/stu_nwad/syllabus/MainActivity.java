package com.example.stu_nwad.syllabus;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stu_nwad.adapters.ListViewAdapter;

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
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static Object[] objects;     // 用于向显示课表的activity传递数据

    // 控件及常量

    public static final String TAG = "POSTTEST";



    public static final String[] YEARS = {"2012-2013", "2013-2014", "2014-2015", "2015-2016", "2016-2017", "2017-2018"};
    public static final String[] SEMESTER = {"SPRING", "SUMMER", "AUTUMN"};


    private EditText address_edit;
    private EditText username_edit;
    private EditText passwd_edit;
    private Button submit_button;
    private Spinner years_spin_box;
    private Spinner semester_spin_box;
    private ListView syllabus_list_view;

    private void getAllViews(){
        address_edit = (EditText) findViewById(R.id.address_edit);
        username_edit = (EditText) findViewById(R.id.username_edit);
        passwd_edit = (EditText) findViewById(R.id.passwd_edit);
        submit_button = (Button) findViewById(R.id.submit_button);
//        result_text_view = (TextView) findViewById(R.id.result_text_view);
        years_spin_box = (Spinner) findViewById(R.id.year_spin_box);
        semester_spin_box = (Spinner) findViewById(R.id.semester_spin_box);
        syllabus_list_view = (ListView) findViewById(R.id.syllabus_list_view);
    }

    private void setupViews(){
        ArrayAdapter<String> years = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, YEARS);
        ArrayAdapter<String> semesters = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SEMESTER);
        years_spin_box.setAdapter(years);
        semester_spin_box.setAdapter(semesters);
        years_spin_box.setSelection(3);     // 2015-2016
        semester_spin_box.setSelection(2);  // AUTUMN
        syllabus_list_view.setAdapter(new ListViewAdapter(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_);

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
            SyllabusGetter syllabusGetter = new SyllabusGetter(requestURL);
//             先判断有无之前保存的文件
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
                syllabusGetter.display(json_data);
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
            syllabusGetter.execute(postData);
        }
    }

    /**
     * 用于异步发送网络请求
     */
        class SyllabusGetter extends AsyncTask<HashMap<String, String>, Void, String> {

//            private TextView view;
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
                String  response = performPostCall(requestURL, params[0]);
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
                    MainActivity.objects = classParser.objs;
                    Log.d(TAG, "established adapter");
//                    mAdapter = new MyAdapter(objs);
//                    mRecyclerView.setAdapter(mAdapter);
                    Intent syllabus_activity = new Intent(MainActivity.this, SyllabusActivity.class);
                    startActivity(syllabus_activity);
                    Toast.makeText(MainActivity.this, "读取课表成功哟~~~~正在保存到文件中~~~~", Toast.LENGTH_SHORT).show();

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
//                }else{
////                    Toast.makeText(MainActivity.this, "解析json数据失败，请重试一下哟~~~~", Toast.LENGTH_SHORT).show();
                }
            }

            public String  performPostCall(String requestURL,
                                           HashMap<String, String> postDataParams) {
                URL url;
                String response = "";
                final int timeout = 3000; // 3s
                try {
                    url = new URL(requestURL);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(timeout);
                    conn.setConnectTimeout(timeout);
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
                    // 不能在这里用 Toast 因为运行的时候这个函数不在主线程被调用
//                    Toast.makeText(MainActivity.this, "没能成功连接到服务器呢~~~~重试一下吧~~~~", Toast.LENGTH_SHORT).show();
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
