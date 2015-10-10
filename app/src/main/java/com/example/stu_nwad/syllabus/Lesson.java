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

    // 额外的信息
    public int start_year;
    public int end_year;
    public int semester;

//    self.class_id = str(start_year) + "_" + str(end_year) + "_" + str(semester) + "_" + self.class_number

    public String generate_class_id(){
        return start_year + "_" + end_year + "_" + semester + "_" + id;
    }

    @Override
    public String toString(){
        // 去掉课程的[课程号]
        int s_index = name.indexOf(']');
        String name_without_prefix = name;
        if (s_index != -1){
            name_without_prefix = name.substring(s_index + 1);
        }
        return name_without_prefix + "[" + room + "]" /* + days.toString() */;
    }

    // 用于表现这个课程的完整信息
    public String representation(){
        return  start_year + "-" + end_year + "\n" + "\n班号:" + id + "\n课程名: " + name + "\n教师: " + teacher + "\n" + "教室: " + room + "\n" + "上课周数: " + duration + "\n学分: " + credit;
    }

    // 主要用于显示周末课程时候的表达呢
    public String toText(){
        return name + "(学分)" + credit + "(" + teacher + ")@" + room + ": " + days.toString();
    }
}
