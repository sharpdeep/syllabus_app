package com.example.stu_nwad.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stu_nwad.syllabus.FileOperation;
import com.example.stu_nwad.syllabus.Lesson;
import com.example.stu_nwad.syllabus.R;

/**
 * Created by STU_nwad on 2015/10/5.
 */
public class ClassDialog extends Dialog implements View.OnClickListener{

    Context context;

    private TextView class_info_text_view;
    private Button submit_button;
    private EditText comment_area;
    private String comment = "";

    private Lesson lesson;

    public ClassDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ClassDialog(Context context, int themeId){
        super(context, themeId);
        this.context = context;
        setContentView(R.layout.dialog_layout);
        find_views();
        submit_button.setOnClickListener(this);

    }

    public void setLesson(Lesson lesson){
        this.lesson = lesson;
//        Log.d(MainActivity.TAG, lesson.representation());
        class_info_text_view.setText(lesson.representation());
        comment = load_comment();
        if (comment != null) {
            comment_area.setText(comment);
            comment_area.setSelection(comment.length());
        }else{
            comment_area.setText("");
        }
    }


    private void find_views(){
        class_info_text_view = (TextView) findViewById(R.id.dialog_content);
        submit_button = (Button) findViewById(R.id.dialog_submit_button);
        comment_area = (EditText) findViewById(R.id.dialog_class_comment);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show(){
        // show 之后才调用 onCreate的
        super.show();
    }

    private boolean save_comment(){
        if (comment_area.getText().toString().isEmpty())
            return false;
        // 内容没有改变
        if (comment_area.getText().toString().equals(comment))
            return false;
        String info = MainActivity.info_about_syllabus;
        String username = info.split(" ")[0];
        String filename = FileOperation.generate_class_file_name(username, lesson.id, "_");
        Log.d(MainActivity.TAG, "saving file" + filename);
        return FileOperation.save_to_file(context, filename, comment_area.getText().toString());
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
        save_comment();
        dismiss();
    }
}
