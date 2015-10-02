package com.example.stu_nwad.syllabus;

import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/9/23.
 */
public class Lesson {
    public String name;
    public String id;
    public String teacher;
    public String room;
    public String duration;
    public String credit;
    public HashMap<String, String> days;

    @Override
    public String toString(){
        // 去掉课程的[课程号]
        int s_index = name.indexOf(']');
        if (s_index != -1){
            name = name.substring(s_index + 1);
        }
        return name + "[" + room + "]" /* + days.toString() */;
    }

    // 用于表现这个课程的完整信息
    public String representation(){
        return "课程名: " + name + "\n教师: " + teacher + "\n" + "教室: " + room + "\n" + "上课周数: " + duration + "\n学分: " + credit;
    }

    // 返回表示课程的字符串呢
    public String toText(){
        return name + "@" + room + ": " + days.toString();
    }
}
