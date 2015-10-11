package com.example.stu_nwad.syllabus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stu_nwad.activities.MainActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/11.
 */
public class DiscussionPullTask extends AsyncTask<HashMap<String, String>, Void, String>  {

    private String address;
    private Context context;
    private DiscussionHandler discussionHandler;

    public DiscussionPullTask(Context context, DiscussionHandler discussionHandler){
        this.context = context;
        this.discussionHandler = discussionHandler;
        this.address = context.getString(R.string.get_discussion_api);
    }

    public void get_discussion(int count, String class_number, int start_year, int end_year, int semester){

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
        DiscussionParser parser = new DiscussionParser(context);
        ArrayList<Discussion> discussions = parser.parse_json(response);
        Collections.reverse(discussions);
        discussionHandler.deal_with_discussion(discussions);
    }
}
