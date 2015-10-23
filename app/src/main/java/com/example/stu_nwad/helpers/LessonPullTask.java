package com.example.stu_nwad.helpers;

import android.os.AsyncTask;

import com.example.stu_nwad.interfaces.LessonHandler;

import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/10/23.
 */
public class LessonPullTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private LessonHandler lessonHandler;
    private String request_url;

    public LessonPullTask(String url, LessonHandler lessonHandler){
        this.request_url = url;
        this.lessonHandler = lessonHandler;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        return HttpCommunication.performPostCall(request_url, params[0]);
    }

    @Override
    protected void onPostExecute(String raw_data){
        lessonHandler.deal_with_lessons(raw_data);
    }

}
