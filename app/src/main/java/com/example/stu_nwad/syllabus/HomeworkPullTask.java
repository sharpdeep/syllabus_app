package com.example.stu_nwad.syllabus;

/**
 * Created by STU_nwad on 2015/10/11.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stu_nwad.activities.MainActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 从服务器拉取数据
 */
public class HomeworkPullTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private String address;
    private Context context;
    private HomeworkHandler homeworkHandler;

    public HomeworkPullTask(Context context, HomeworkHandler homeworkHandler){
        this.address = context.getString(R.string.get_home_work_api);
        this.context = context;
        this.homeworkHandler = homeworkHandler;
    }

    public void get_homework(int count, String class_number, int start_year, int end_year, int semester){

        HashMap<String, String> data = new HashMap<>();
        data.put("number", class_number);
        data.put("start_year", start_year + "");
        data.put("end_year", end_year + "");
        data.put("semester", semester + "");
        data.put("count", count + "");

        // 拉取数据
        execute(data);
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        try {
            String query_string = HttpCommunication.get_url_encode_string(params[0]);
            Log.d(MainActivity.TAG, query_string);
            return HttpCommunication.perform_get_call(address + "?" + query_string);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String response){
        HomeworkParser parser = new HomeworkParser(context);
        ArrayList<Homework> all_homework = parser.parser_json(response);
        homeworkHandler.deal_with_homework(all_homework);
    }

}
